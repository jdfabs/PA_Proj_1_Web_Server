package core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A thread pool that manages a fixed number of worker threads to execute tasks.
 * <p>
 * This class initializes a pool of {@link WorkerThread} instances that continuously
 * fetch and execute {@link Runnable} tasks from a shared {@link BlockingQueue}.
 * Tasks are submitted using the {@link #execute(Runnable)} method.
 * </p>
 * <p>
 * The thread pool can be gracefully shut down using {@link #shutdown()}, after which
 * no new tasks will be accepted and all threads will be interrupted.
 * </p>
 */
public class ThreadPool {
    /** Array of worker threads that execute submitted tasks. */
    private final WorkerThread[] workers;
    /** A blocking queue that holds tasks to be processed by worker threads. */
    private final BlockingQueue<Runnable> taskQueue;
    /** Indicates whether the thread pool has been shut down. */
    private volatile boolean isShutdown = false;

    /**
     * Constructs a thread pool with the specified number of worker threads.
     * <p>
     * A {@link LinkedBlockingQueue} is used for storing tasks, and each worker
     * thread is started immediately upon creation.
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
     * If the thread pool has not been shut down, the task is added to the task queue
     * and will be picked up by an available worker thread. If the pool is shut down,
     * the task will be ignored.
     * </p>
     *
     * @param task the {@link Runnable} task to be executed
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
     * Shuts down the thread pool, preventing any new tasks from being submitted.
     * <p>
     * All currently running worker threads will be interrupted, and the {@code isShutdown}
     * flag is set to {@code true}.
     * </p>
     */
    public void shutdown() {
        isShutdown = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    /**
     * Returns the array of worker threads currently managed by this thread pool.
     *
     * @return an array of {@link WorkerThread} instances
     */
    public WorkerThread[] getWorkers() {
        return workers;
    }
}