package utils;

import logging.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * The {@code FileMonitor} class implements a monitor pattern to ensure exclusive, thread-safe,
 * and FIFO access to files identified by their path,name and extension.
 */
public class FileMonitor implements LogProducer {

    private static final ConcurrentHashMap<String, Lock> fileLocks = new ConcurrentHashMap<>();

    /**
     * Constructs a {@code FileMonitor} with the specified logger.
     */
    public FileMonitor() {
    }

    /**
     * Acquires an exclusive lock for the specified file.
     * Other thread gain access FIFO (due to the ReentrantLock(true)).
     * If the lock doesn't already exist, it's created before locking.
     *
     * @param fileName The path of the file to lock.
     */
    public void lockFile(String fileName) {
        Lock lock = fileLocks.computeIfAbsent(fileName, k -> new ReentrantLock(true));
        lock.lock();
    }

    /**
     * Releases the lock held for the specified file.
     * <p>
     * If no lock is found for the specified file, an error is logged.
     * </p>
     *
     * @param fileName The path of the file to unlock.
     */
    public void unlockFile(String fileName) {
        Lock lock = fileLocks.get(fileName);
        if (lock != null) {
            lock.unlock();
        } else {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, "Lock for file \"" + fileName + "\" was not found"));
        }
    }
}
