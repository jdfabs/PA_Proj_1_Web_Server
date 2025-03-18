package logging;

public interface LogProducer extends SharedBuffer {
    public default void logMessage(LoggingTask task) {
        buffer.add(task);
    }
}
