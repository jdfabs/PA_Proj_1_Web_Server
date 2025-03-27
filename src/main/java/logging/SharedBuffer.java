package logging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Shared interface containing a global blocking queue for logging tasks.
 * <p>
 * This buffer is used by both log producers and the {@link Logger} thread to
 * asynchronously pass log entries. All classes implementing this interface
 * share the same static buffer instance.
 */
public interface SharedBuffer {
    /**
     * The global blocking queue used for logging task handoff.
     * Implementations of {@link LogProducer} add to this queue,
     * while the {@link Logger} thread polls and processes entries from it.
     */
    BlockingQueue<LoggingTask> buffer = new LinkedBlockingQueue<>();
}
