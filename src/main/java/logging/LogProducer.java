package logging;

public interface LogProducer extends SharedBuffer {
    default void logMessage(LoggingTask task) {
        buffer.add(task);
    }
}
