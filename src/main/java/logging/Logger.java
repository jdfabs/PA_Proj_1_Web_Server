package logging;

import com.sun.jdi.InvalidTypeException;
import config.ServerConfig;
import utils.FileMonitor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * A background logging thread that asynchronously processes log messages from a shared buffer.
 * <p>
 * Supports logging to the console (stdout, stderr) and to a log file, including structured
 * JSON request logging. Logging behavior is determined by {@link LogType} and {@link LogLocation}.
 * </p>
 * <p>
 * Implements {@link SharedBuffer} to consume messages from the global buffer and {@link LogProducer}
 * to allow recursive error logging if necessary.
 * </p>
 */
public class Logger extends Thread implements SharedBuffer, LogProducer {
    /** Controls whether the logger is actively processing log messages. */
    private volatile boolean running = true;
    /** Full path to the log file. */
    private final String logPath;

    /**
     * Constructs a new {@code Logger} instance based on the provided server configuration.
     *
     * @param config the {@link ServerConfig} providing log path and log file name
     */
    public Logger(ServerConfig config) {
        this.logPath = System.getProperty("user.dir") + config.getLogPath() + "/" + config.getLogFileName() + ".log";
    }

    /**
     * Continuously polls the shared buffer for new log messages and processes them.
     * <p>
     * Log messages are dispatched based on their type (info, error, warning, request)
     * and output location (console, file). This method runs in a loop until {@link #shutdown()}
     * is called or the thread is interrupted.
     * </p>
     */
    @Override
    public void run() {
        while (running) {
            try {
                LoggingTask loggingTask = buffer.poll(100, TimeUnit.MILLISECONDS);

                if (loggingTask == null) continue;

                switch (loggingTask.getType()) {
                    case Info -> info(loggingTask.getLocation(), loggingTask.getMessage());
                    case Error -> error(loggingTask.getLocation(), loggingTask.getMessage());
                    case Warning -> warning(loggingTask.getLocation(), loggingTask.getMessage());
                    case Request -> request(loggingTask.getLocation(),loggingTask.getMessage() + " " + loggingTask.getRequestTime());
                    default -> throw new InvalidTypeException();
                }
            } catch (InvalidTypeException | InterruptedException e) {
                logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, e.getMessage()));
            }
        }
    }

    /**
     * Logs a structured HTTP request in JSON format.
     *
     * @param location the location to output the log (e.g., file)
     * @param message  the raw request log string containing method, route, status, origin, and timestamp
     */
    private void request(LogLocation location, String message) {
        String[] tokens = message.split(" ");

        String method = tokens[0];
        String route = tokens[1];
        String origin = tokens[tokens.length - 2];
        String httpStatus = tokens[tokens.length - 3];
        String timestamp = tokens[tokens.length - 1];

        String logMessage = String.format(
                "{\"timestamp\":\"%s\",\"method\":\"%s\",\"route\":\"%s\",\"origin\":\"%s\",\"status\":%s}",
                timestamp, method, route, origin, httpStatus
        );

        logToLocation(location, logMessage);
    }


    /**
     * Logs an informational message.
     *
     * @param location the output destination
     * @param message  the message to log
     */
    private void info(LogLocation location, String message) {
        String logMessage = "[INFO] " + message;

        logToLocation(location, logMessage);
    }

    /**
     * Logs an error message.
     *
     * @param location the output destination
     * @param message  the message to log
     */
    private void error(LogLocation location, String message) {
        String logMessage = "[ERROR] " + message;

        logToLocation(location, logMessage);
    }

    /**
     * Logs a warning message.
     *
     * @param location the output destination
     * @param message  the message to log
     */
    private void warning(LogLocation location, String message) {
        String logMessage = "[WARNING] " + message;

        logToLocation(location, logMessage);
    }

    /**
     * Dispatches a formatted log message to the specified location.
     *
     * @param location the log destination
     * @param message  the message to write
     */
    private void logToLocation(LogLocation location, String message) {
        switch (location) {
            case ConsoleOut -> logConsoleOut(message);
            case ConsoleErr -> logConsoleErr(message);
            case File -> logFile(message);
            default -> System.err.println("Invalid log location: " + location + " message: " + message);
        }
    }

    /**
     * Logs a message to standard output.
     *
     * @param message the message to print
     */
    private void logConsoleOut(String message) {
        System.out.println(message);
    }

    /**
     * Logs a message to standard error.
     *
     * @param message the message to print
     */
    private void logConsoleErr(String message) {
        System.err.println(message);
    }

    /**
     * Appends a JSON-formatted log message to the configured log file.
     * <p>
     * This method ensures that the log file always maintains a valid JSON array format.
     * If the log file does not exist or is empty, a new file is created with a JSON array
     * containing the provided log message as its first element. If the file already exists,
     * the method reads the current content, removes the final closing bracket, appends a comma
     * if necessary, inserts the new log message, and then re-adds the closing bracket.
     * </p>
     * <p>
     * The parent directory is created if it does not exist. All file access is synchronized via
     * a {@code FileMonitor} to protect against concurrent modifications. Additionally, to avoid
     * delaying user responses, it is recommended that the invocation of this method be performed
     * asynchronously.
     * </p>
     *
     * @param message the JSON-formatted log message to write to the file; this should be a valid
     *                JSON object (without the trailing comma) that conforms to the expected format.
     */
    private void logFile(String message) {
        FileMonitor fileMonitor = new FileMonitor();
        fileMonitor.lockFile(logPath);
        File logFile = new File(logPath);

        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            if (!logFile.exists() || logFile.length() == 0) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                    writer.write("[\n" + message + "\n]");
                }
            } else {
                String content = new String(Files.readAllBytes(logFile.toPath()), StandardCharsets.UTF_8);
                content = content.trim();
                if (!content.endsWith("]")) {
                    throw new IOException("Log file is not in the correct JSON format.");
                }

                int lastBracketIndex = content.lastIndexOf("]");
                String contentWithoutClosing = content.substring(0, lastBracketIndex).trim();

                String separator = contentWithoutClosing.equals("[") ? "" : ",";
                String newContent = contentWithoutClosing + separator + "\n" + message + "\n]";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                    writer.write(newContent);
                }
            }
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, e.getMessage()));
        } finally {
            fileMonitor.unlockFile(logPath);
        }
    }

    /**
     * Stops the logger thread by setting its {@code running} flag to false.
     *
     * @return {@code this} for convenient chaining (e.g., with {@code join()})
     */
    public Logger shutdown() {
        running = false;
        return this; //self return for thread join convenience #LINQIsLove
    }
}