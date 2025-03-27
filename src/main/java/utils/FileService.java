package utils;

import Cache.CacheManager;
import Cache.CacheManagerSingleton;
import config.ServerConfig;
import logging.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A thread-based file reader that handles file access with concurrency and caching.
 * <p>
 * The {@code FileService} reads content from the filesystem in a thread-safe manner
 * using {@link FileMonitor} to prevent concurrent access to the same file.
 * <p>
 * It also interacts with {@link CacheManager} to serve cached content if available,
 * reducing disk I/O and improving response time.
 */
public class FileService extends Thread implements LogProducer {
    /** A global file monitor used to coordinate exclusive file access. */
    private static final FileMonitor fileMonitor = new FileMonitor();
    /** The fully resolved file system path to the target file. */
    private final String path;
    /** The content of the file, once read. */
    private byte[] content;

    /**
     * Constructs a {@code FileService} instance and resolves the full path of the target file.
     * <p>
     * If the path ends with a slash, the default page name and extension are appended
     * based on the provided {@link ServerConfig}.
     *
     * @param config the server configuration containing document root and default file info
     * @param path   the requested route or file path (relative)
     */
    public FileService(ServerConfig config, String path) {
        if (path.endsWith("/")) {
            path += config.getDefaultPageFile() + "." + config.getDefaultPageExtension();
            this.path = config.getDocumentRoot() + path;
        } else {
            this.path = config.getDocumentRoot() + path;
        }
    }


    /**
     * Executes the file read operation in a thread-safe, cache-aware manner.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Checks if the file content is already in cache</li>
     *     <li>If not, locks the file using {@link FileMonitor} to avoid race conditions</li>
     *     <li>Performs a second cache check after acquiring the lock</li>
     *     <li>If still uncached, reads from disk and updates the cache</li>
     *     <li>Logs the result and releases the lock</li>
     * </ol>
     * <p>
     * If any error occurs during file reading, empty content is returned and the error is logged.
     */
    @Override
    public void run() {
        CacheManager cacheManager = CacheManagerSingleton.getInstance();

        byte[] cachedContent = cacheManager.readFromCache(path);
        if (cachedContent != null) {
            content = cachedContent;
            logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Served from cache: " + path));
            return;
        }

        fileMonitor.lockFile(path);
        try {
            // 2nd check: maybe another thread wrote to cache while we were waiting for the lock
            cachedContent = cacheManager.readFromCache(path);
            if (cachedContent != null) {
                content = cachedContent;
                logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Served from cache (after lock): " + path));
                return;
            }

            content = Files.readAllBytes(Paths.get(path));
            cacheManager.writeToCache(path, content);
            logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Read from disk and cached: " + path));

        } catch (IOException e) {
            content = new byte[0];
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Error reading file: " + e.getMessage()));
        } finally {
            logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Done Reading File: " + path));
            fileMonitor.unlockFile(path);
        }
    }

    /**
     * Returns the content read from the file.
     *
     * @return a byte array containing the file's content.
     */
    public byte[] getContent() {
        return content;
    }
}