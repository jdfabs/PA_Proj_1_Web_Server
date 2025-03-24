package core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final int poolSize;
    private final WorkerThread[] workers;
    private final BlockingQueue<Runnable> taskQueue;
    private volatile boolean isShutdown = false;

    public ThreadPool(int poolSize) {
        this.poolSize = poolSize;
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new WorkerThread[poolSize];

        // Starts workers
        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread(taskQueue);
            workers[i].start();
        }
    }

    public void execute(Runnable task) {
        if (!isShutdown) {
            try {
                taskQueue.put(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        isShutdown = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    public int getPoolSize() {
        return poolSize;
    }

    public WorkerThread[] getWorkers() {
        return workers;
    }

    public BlockingQueue<Runnable> getTaskQueue() {
        return taskQueue;
    }

    public boolean isShutdown() {
        return isShutdown;
    }
}