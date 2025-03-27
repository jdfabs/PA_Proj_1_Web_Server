package Cache;

import logging.LogLocation;
import logging.LogProducer;
import logging.LogType;
import logging.LoggingTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages a thread-safe in-memory cache using a reader-writer pattern.
 * <p>
 * This cache manager supports concurrent reads and exclusive writes,
 * along with automatic expiration of cache entries based on their last access time.
 * A background thread continuously monitors and removes expired entries.
 */
public class CacheManager extends Thread implements LogProducer {
    /** Duration after which cache entries expire (default: 30 seconds). */
    private static Duration expirationTime = Duration.ofSeconds(30);

    /** The main cache structure mapping file paths to their cached content entries. */
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    /** Semaphore for exclusive write access. */
    private final Semaphore writeLock = new Semaphore(1, true);
    /** Lock to protect readerCount updates. */
    private final Lock readLock = new ReentrantLock(true);

    /** Number of readers currently accessing the cache. */
    private int readerCount = 0;

    /**
     * Starts a background thread to monitor and remove expired cache entries.
     * The thread checks all entries and removes any that are older than {@code expirationTime}.
     */
    @Override
    public void run() {
        while (true) {
            for (String path : cache.keySet()) {
                CacheEntry entry = cache.get(path);
                if(entry.getLastUseTime().plus(expirationTime).isBefore(LocalDateTime.now())){
                    removeFromCache(path);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    /**
     * Reads file content from the cache in a thread-safe manner.
     * If the entry is not present or expired, returns {@code null}.
     *
     * @param path the file path or identifier
     * @return the cached content, or null if not present or expired
     */
    public byte[] readFromCache(String path) {
        try {
            readLock.lock();
            readerCount++;
            if (readerCount == 1) {
                writeLock.acquire();
            }
            readLock.unlock();

            CacheEntry entry = cache.get(path);

            return (entry == null) ? null : entry.getContent();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            try {
                readLock.lock();
                readerCount--;
                if (readerCount == 0) {
                    writeLock.release();
                }
                readLock.unlock();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Writes file content to the cache in a thread-safe manner.
     *
     * @param path   the file path
     * @param content the file content to cache
     */
    public void writeToCache(String path, byte[] content) {
        try {
            writeLock.acquire();
            cache.put(path, new CacheEntry(content));
            logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Cache entry created: " + path ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            writeLock.release();
        }
    }

    /**
     * Clears an {@link CacheEntry} from cache in a thread-safe manner.
     *
     * @param path   the file path
     */
    private void removeFromCache(String path) {
        try {
            writeLock.acquire();
            cache.remove(path);
            logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Cache expired: " + path ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            writeLock.release();
        }
    }

    /**
     * Updates the expiration time for cache entries.
     *
     * @param expirationTime a {@link Duration} specifying how long entries remain valid
     */
    public void setExpirationTime(Duration expirationTime) {
        this.expirationTime = expirationTime;
    }
}