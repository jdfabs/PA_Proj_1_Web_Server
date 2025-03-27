package core;

import logging.LogLocation;
import logging.LogProducer;
import logging.LogType;
import logging.LoggingTask;

import java.util.concurrent.BlockingQueue;

/**
 * A worker thread that continuously executes tasks from a shared task queue.
 * <p>
 * This class extends {@link Thread} and implements {@link LogProducer} for logging task errors.
 * It repeatedly takes {@link Runnable} tasks from a {@link BlockingQueue} and executes them.
 * If the thread is interrupted or explicitly shut down, it stops processing new tasks.
 * </p>
 */

public class WorkerThread extends Thread implements LogProducer {
    /** The queue containing tasks to be executed by this thread. */
    private final BlockingQueue<Runnable> taskQueue;
    /** Flag indicating whether this thread has been shut down. */
    private volatile boolean isShutdown = false;

    /**
     * Constructs a new {@code WorkerThread} that pulls tasks from the specified task queue.
     *
     * @param taskQueue the blocking queue from which tasks will be retrieved and executed
     */
    public WorkerThread(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

    /**
     * Continuously retrieves and executes tasks from the queue until the thread is shut down.
     * <p>
     * If interrupted, the thread exits gracefully. Any exceptions thrown during task execution
     * are caught and logged using the logging system.
     * </p>
     */
    @Override
    public void run() {
        while (!isShutdown) {
            try {
                Runnable task = taskQueue.take();
                task.run();
            } catch (InterruptedException e) {
                isShutdown = true;
                break;
            } catch (Exception e) {
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
