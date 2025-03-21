package logging;

import com.sun.jdi.InvalidTypeException;

import java.util.concurrent.TimeUnit;

/**
 * A logger that prints log messages.
 * This class will later be expanded to log to the log file!!
 */
public class Logger extends Thread implements SharedBuffer , LogProducer{
    private volatile boolean running = true;

    public void run() {
        while (running) {
            try {
                LoggingTask loggingTask = buffer.poll(100, TimeUnit.MILLISECONDS);

                if (loggingTask == null) continue;

                switch (loggingTask.getType()) {
                    case Info:
                        info(loggingTask.getMessage());
                        break;
                    case Error:
                        error(loggingTask.getMessage());
                        break;
                    case Warning:
                        warning(loggingTask.getMessage());
                        break;
                    default:
                        throw new InvalidTypeException();
                }
            } catch (InvalidTypeException | InterruptedException e) {
                logMessage(new LoggingTask(LogType.Error, LogLocation.Console, e.getMessage()));
            }
        }
    }


    /**
     * Prints Information
     *
     * @param message The message which will be on the log
     */
    private void info(String message) {
        System.out.println("[INFO] " + message);
    }

    /**
     * Prints Errors
     *
     * @param message The message which will be on the log
     */
    private void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    /**
     * Prints Warnings
     *
     * @param message The message which will be on the log
     */
    private void warning(String message) {
        System.err.println("[WARNING] " + message);
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