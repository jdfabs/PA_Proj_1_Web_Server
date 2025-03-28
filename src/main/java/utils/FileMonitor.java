package utils;

import logging.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ensures exclusive, FIFO-based access to individual files using per-file locks.
 * <p>
 * The {@code FileMonitor} class implements a monitor-style synchronization mechanism
 * using {@link ReentrantLock}s mapped to file paths. Each file has its own lock,
 * allowing concurrent file access across different files while ensuring that
 * access to the same file is strictly serialized.
 * </p>
 * <p>
 * Locks are created on demand and stored in a shared {@link ConcurrentHashMap}.
 * FIFO ordering is enforced using {@code ReentrantLock(true)}.
 */
public class FileMonitor implements LogProducer {

    /** A map of file paths to their associated reentrant locks. */
    private static final ConcurrentHashMap<String, Lock> fileLocks = new ConcurrentHashMap<>();

    /**
     * Acquires an exclusive FIFO lock for the specified file path.
     * <p>
     * If a lock does not already exist for the file, one is created with fairness enabled.
     * This guarantees that threads will acquire the lock in the order they requested it.
     * </p>
     *
     * @param fileName the path of the file to lock
     */
    public void lockFile(String fileName) {
        Lock lock = fileLocks.computeIfAbsent(fileName, k -> new ReentrantLock(true));
        lock.lock();
    }

    /**
     * Releases the lock held for the specified file.
     * <p>
     * If no lock is found for the file, an error is logged.
     * </p>
     *
     * @param fileName the path of the file to unlock
     */
    public void unlockFile(String fileName) {
        Lock lock = fileLocks.get(fileName);
        if (lock != null) {
            lock.unlock();
        } else {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Lock for file \"" + fileName + "\" was not found"));
        }
    }
}
