package logging;

import com.sun.jdi.InvalidTypeException;
import config.ServerConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
                "{\"timestamp\":\"%s\",\"method\":\"%s\",\"route\":\"%s\",\"origin\":\"%s\",\"status\":%s},",
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
     * Appends a message to the configured log file.
     * <p>
     * Ensures the parent directory exists before writing. Errors encountered while
     * writing to the file are logged to the console.
     *
     * @param message the message to write to the file
     */
    private void logFile(String message) {
        File logFile = new File(logPath);

        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write( message);
            writer.newLine();
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, e.getMessage()));
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