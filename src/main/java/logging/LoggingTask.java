package logging;


/**
 * task that contains all the components of the message to be logged
 */
public class LoggingTask {

    private final LogType type;
    private final LogLocation location;
    private final String message;

    public LoggingTask(LogType type, LogLocation location, String message) {
        this.type = type == null ? LogType.Info : type;
        this.location = location == null? LogLocation.Console : location;
        this.message = message == null ? "" : message;
    }

    /**
     * gets the type of the log
     *
     * @return the type of log to be displayed
     */
    public LogType getType() {
        return type;
    }

    public LogLocation getLocation() {
        return location;
    }

    /**
     * gets the message of the log
     *
     * @return the body of the log
     */
    public String getMessage() {
        return message;
    }
}
