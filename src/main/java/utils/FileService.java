package utils;

import Cache.CacheManager;
import Cache.CacheManagerSingleton;
import config.ServerConfig;
import logging.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A simple file reader.
 * It is responsible for handling file operations.
 */
public class FileService extends Thread implements LogProducer {
    private static final FileMonitor fileMonitor = new FileMonitor();
    private final String path;
    private byte[] content;

    /**
     * Constructor to initialize {@code FileService} instance with the specified logger.
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
     * Reads the file content in a thread-safe manner similar to what has been discussed in class.
     * This method is executed when the thread is started.
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
            content = Files.readAllBytes(Paths.get(path));
            cacheManager.writeToCache(path, content);
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Error reading file: " + e.getMessage()));
            content = new byte[0];
        } finally {
            fileMonitor.unlockFile(path);
        }
        logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "DONE READING FILE: " + path));
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