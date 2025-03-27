package logging;


import java.time.LocalDateTime;


/**
 * Represents a single log task containing all necessary information to be logged.
 * <p>
 * This class encapsulates the log type, destination, message content, and timestamp.
 * It is used by the logging system to defer and centralize log writing asynchronously.
 */
public class LoggingTask {
    /**
     * The type or severity of the log (e.g., Info, Error, Request).
     */
    private final LogType type;
    /**
     * The location where the log should be written (e.g., ConsoleOut, File).
     */
    private final LogLocation location;
    /**
     * The log message content.
     */
    private final String message;
    /**
     * The timestamp indicating when the log task was created.
     */
    private final LocalDateTime requestTime;

    /**
     * Constructs a {@code LoggingTask} with the given type, location, and message.
     * If any parameter is {@code null}, a sensible default is applied:
     * <ul>
     *     <li>Type defaults to {@link LogType#Info}</li>
     *     <li>Location defaults to {@link LogLocation#ConsoleOut}</li>
     *     <li>Message defaults to an empty string</li>
     * </ul>
     *
     * @param type     the severity/type of log (can be {@code null})
     * @param location the destination of the log (can be {@code null})
     * @param message  the content of the log (can be {@code null})
     */
    public LoggingTask(LogType type, LogLocation location, String message) {
        this.type = type == null ? LogType.Info : type;
        this.location = location == null ? LogLocation.ConsoleOut : location;
        this.message = message == null ? "" : message;
        this.requestTime = LocalDateTime.now();
    }

    /**
     * Returns the type of this log task.
     *
     * @return the {@link LogType} representing the log severity or category
     */
    public LogType getType() {
        return type;
    }

    /**
     * Returns the output location for this log task.
     *
     * @return the {@link LogLocation} where the log should be written
     */
    public LogLocation getLocation() {
        return location;
    }

    /**
     * Returns the message content of the log.
     *
     * @return the log message string
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the timestamp of when the log task was created.
     *
     * @return a {@link LocalDateTime} representing the request time
     */
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
}
