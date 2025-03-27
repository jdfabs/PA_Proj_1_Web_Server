package logging;

/**
 * Represents the output destination for a log message.
 * <p>
 * Used by the {@link logging.Logger} to determine where to write the log.
 */
public enum LogLocation {
    ConsoleOut,
    ConsoleErr,
    File,
}
