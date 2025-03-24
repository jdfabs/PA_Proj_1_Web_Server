package core;

import logging.LogLocation;
import logging.LogProducer;
import logging.LogType;
import logging.LoggingTask;
import java.util.concurrent.BlockingQueue;

/**
 * A thread that processes tasks from a blocking queue until interrupted or an error occurs.
 * <p>
 * This class extends {@link Thread} and implements {@link LogProducer} to handle task execution
 * and logging. It continuously retrieves tasks from a {@link BlockingQueue} and executes them
 * until an interruption occurs or the thread is shut down. Errors during task execution are
 * logged using the {@link LogProducer} interface.
 * </p>
 */

public class WorkerThread extends Thread implements LogProducer {
    private final BlockingQueue<Runnable> taskQueue;
    private volatile boolean isShutdown = false;

    /**
     * Constructs a new WorkerThread with the specified task queue.
     * <p>
     * The provided queue is used to retrieve tasks that the thread will execute.
     * </p>
     *
     * @param taskQueue the queue from which tasks are retrieved
     */

    public WorkerThread(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    /**
     * Executes tasks from the task queue in a loop until shutdown or interruption.
     * <p>
     * This method continuously takes tasks from the queue and runs them. If an
     * {@link InterruptedException} occurs, the thread sets the shutdown flag and exits.
     * Other exceptions during task execution are caught, logged as errors, and the loop
     * continues to process subsequent tasks.
     * </p>
     *
     * @see Thread#run()
     */

    @Override
    public void run() {
        while (!isShutdown) {
            try {
                Runnable task = taskQueue.take();
                task.run();
            } catch (InterruptedException e) {
                // If thread was interrupted, leaves loop
                isShutdown = true;
                break;
            } catch (Exception e) {
                // Error log if task fails
                logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr,
                        "Task execution error: " + e.getMessage()));
            }
        }
    }
    /**
     * Checks if the worker thread has been shut down.
     * <p>
     * This method returns the current shutdown status of the thread, which is set
     * when the thread is interrupted or explicitly stopped.
     * </p>
     *
     * @return <code>true</code> if the thread is shut down, <code>false</code> otherwise
     */
    public boolean isShutdown() {
        return isShutdown;
    }
}
