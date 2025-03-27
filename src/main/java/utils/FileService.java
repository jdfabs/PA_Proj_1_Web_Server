package utils;

import logging.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


/**
 * A simple file reader.
 * It is responsible for handling file operations.
 */
public class FileService extends Thread implements LogProducer {
    private static final Logger logger = new Logger();
    private static final FileMonitor fileMonitor = new FileMonitor();
    private final String path;
    private byte[] content;

    /**
     * Constructor to initialize {@code FileService} instance with the specified logger.
     */
    public FileService(String path) {
        this.path = path;
    }

    /**
     * Reads the file content in a thread-safe manner similar to what has been discussed in class.
     * This method is executed when the thread is started.
     */

    @Override
    public void run() {
        fileMonitor.lockFile(path);
        try {
            content = Files.readAllBytes(Paths.get(path));

            //Faking long tasks

            logMessage(new LoggingTask(LogType.Test, LogLocation.Console, "Computing extremely hard file reading bip bop"));
            Thread.sleep(new Random().nextInt(500, 9999));
            //Finish Faking long tasks
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, "Error reading file: " + e.getMessage()));
            content = new byte[0];
        } catch (InterruptedException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, e.getMessage()));
            content = new byte[0];
        } finally {
            fileMonitor.unlockFile(path);
        }
        logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "Done Reading File: " + path));
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