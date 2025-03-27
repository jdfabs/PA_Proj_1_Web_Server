package logging;

/**
 * Represents the severity or category of a log message.
 * <p>
 * Used by the {@link logging.LoggingTask} to determine how the message should be handled and formatted.
 */
public enum LogType {
    Warning,
    Error,
    Info,
    Request
}
