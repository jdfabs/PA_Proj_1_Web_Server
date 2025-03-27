package core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread pool that manages a fixed number of worker threads to execute tasks.
 * <p>
 * This class initializes a pool of {@link WorkerThread} instances that process tasks
 * submitted to a shared {@link BlockingQueue}. Tasks are added via the
 * {@link #execute(Runnable)} method and executed by available worker threads. The pool
 * can be shut down using the {@link #shutdown()} method, after which no new tasks are accepted.
 * </p>
 */
public class ThreadPool {
    private final int poolSize;
    private final WorkerThread[] workers;
    private final BlockingQueue<Runnable> taskQueue;
    private volatile boolean isShutdown = false;

    /**
     * Creates a thread pool with the specified number of worker threads.
     * <p>
     * Initializes a {@link LinkedBlockingQueue} for task storage and starts the specified
     * number of {@link WorkerThread} instances to process tasks from the queue.
     * </p>
     *
     * @param poolSize the number of worker threads in the pool
     */
    public ThreadPool(int poolSize) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new WorkerThread[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread(taskQueue);
            workers[i].start();
        }
    }

    /**
     * Submits a task to the thread pool for execution.
     * <p>
     * If the pool is not shut down, the task is added to the task queue. If an
     * {@link InterruptedException} occurs while adding the task, the thread's interrupt
     * status is restored.
     * </p>
     *
     * @param task the task to be executed by a worker thread
     */
    public void execute(Runnable task) {
        if (!isShutdown) {
            try {
                taskQueue.put(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Shuts down the thread pool, interrupting all worker threads.
     * <p>
     * After this method is called, no new tasks can be submitted, and all worker threads
     * are interrupted to stop task processing. The shutdown flag is set to <code>true</code>.
     * </p>
     */
    public void shutdown() {
        isShutdown = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    /**
     * Returns the number of worker threads in the pool.
     *
     * @return the pool size
     */
    public WorkerThread[] getWorkers() {
        return workers;
    }
}