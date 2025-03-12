package utils;

import logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * A simple file reader.
 * It is responsible for handling file operations.
 */
public class FileService {
    private Logger logger;

    /**
     * Constructor to initialize {@code FileService} instance with the specified logger.
     *
     * @param logger the {@link Logger} instance to be used for logging errors.
     */
    public FileService(Logger logger) {
        this.logger = logger;
    }

    /**
     * Reads a binary file and returns its contents as a byte array.
     *
     * @param path The file path to read.
     * @return A byte array containing the file's contents, or an empty array if an error occurs.
     */
    public byte[] readFile(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            logger.error("Error reading file: " + e.getMessage());
            //e.printStackTrace();
            return new byte[0];
        }
    }
}