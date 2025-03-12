package logging;

/**
 * A logger that prints log messages.
 * This class will later be expanded to log to the log file!!
 */
public class Logger {

    /**
     * Prints Information
     *
     * @param message
     */
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    /**
     * Prints Errors
     *
     * @param message
     */
    public void error(String message) {
        System.err.println("[ERROR] " + message);
    }
}