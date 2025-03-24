package core;

import logging.LogLocation;
import logging.LogProducer;
import logging.LogType;
import logging.LoggingTask;

import java.util.concurrent.BlockingQueue;

public class WorkerThread extends Thread implements LogProducer {
    private final BlockingQueue<Runnable> taskQueue;
    private volatile boolean isShutdown = false;

    public WorkerThread(BlockingQueue<Runnable> taskQueue) {
        this.taskQueue = taskQueue;
    }

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
                logMessage(new LoggingTask(LogType.Error, LogLocation.Console,
                        "Task execution error: " + e.getMessage()));
            }
        }
    }

    public boolean isShutdown() {
        return isShutdown;
    }
}
