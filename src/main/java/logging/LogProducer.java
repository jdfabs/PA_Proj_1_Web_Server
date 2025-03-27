package logging;

/**
 * Marker interface for classes that generate log messages.
 * <p>
 * Provides a default method to submit log tasks to the shared buffer.
 * Any class implementing this interface can log messages using {@link #logMessage(LoggingTask)}.
 */
public interface LogProducer extends SharedBuffer {
    /**
     * Submits a logging task to the global log buffer.
     *
     * @param task the {@link LoggingTask} to be enqueued for asynchronous logging
     */
    default void logMessage(LoggingTask task) {
        buffer.add(task);
    }
}
