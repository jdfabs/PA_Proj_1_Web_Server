package logging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public interface SharedBuffer {
    BlockingQueue<LoggingTask> buffer = new LinkedBlockingQueue<>();
}
