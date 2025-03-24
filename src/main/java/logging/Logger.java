package logging;

import com.sun.jdi.InvalidTypeException;
import config.ServerConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A logger that prints log messages.
 * This class will later be expanded to log to the log file!!
 */
public class Logger extends Thread implements SharedBuffer, LogProducer {
    private volatile boolean running = true;
    private final String logPath;

    public Logger(ServerConfig config) {
        this.logPath = System.getProperty("user.dir") + config.getLogPath() + "/" + config.getLogFileName() + ".log";
    }


    public void run() {
        while (running) {
            try {
                LoggingTask loggingTask = buffer.poll(100, TimeUnit.MILLISECONDS);

                if (loggingTask == null) continue;

                switch (loggingTask.getType()) {
                    case Info:
                        info(loggingTask.getLocation(), loggingTask.getMessage());
                        break;
                    case Error:
                        error(loggingTask.getLocation(), loggingTask.getMessage());
                        break;
                    case Warning:
                        warning(loggingTask.getLocation(), loggingTask.getMessage());
                        break;
                    default:
                        throw new InvalidTypeException();
                }
            } catch (InvalidTypeException | InterruptedException e) {
                logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, e.getMessage()));
            }
        }
    }


    /**
     * Prints Information
     *
     * @param message The message which will be on the log
     */
    private void info(LogLocation location, String message) {
        String logMessage = "[INFO] " + message;

        logToLocation(location, logMessage);
    }

    /**
     * Prints Errors
     *
     * @param message The message which will be on the log
     */
    private void error(LogLocation location, String message) {
        String logMessage = "[ERROR] " + message;

        logToLocation(location, logMessage);
    }

    /**
     * Prints Warnings
     *
     * @param message The message which will be on the log
     */
    private void warning(LogLocation location, String message) {
        String logMessage = "[WARNING] " + message;

        logToLocation(location, logMessage);
    }

    private void logToLocation(LogLocation location, String message) {
        switch (location) {
            case ConsoleOut:
                logConsoleOut(message);
                break;
            case ConsoleErr:
                logConsoleErr(message);
                break;
            case File:
                logFile(message);
                break;
            default:
                System.err.println("Invalid log location: " + location + " message: " + message);
        }
    }

    private void logConsoleOut(String message) {
        System.out.println(message);
    }

    private void logConsoleErr(String message) {
        System.err.println(message);
    }

    private void logFile(String message) {
        File logFile = new File(logPath);

        File parentDir = logFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(timestamp + " " + message);
            writer.newLine();
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, e.getMessage()));
        }
    }

    /**
     * Running flag turns to false, making instance to shut down
     *
     * @return {@code self} for quick instance referenciation
     */
    public Logger shutdown() {
        running = false;
        return this; //self return for thread join convenience #LINQIsLove
    }
}