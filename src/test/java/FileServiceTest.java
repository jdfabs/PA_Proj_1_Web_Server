import config.ServerConfig;
import logging.SharedBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {
    private ServerConfig config;

    @BeforeEach
    void setUp() {
        // Load config file for tests (from src/test/resources)
        config = new ServerConfig("src/test/java/resources/server.config");
        SharedBuffer.buffer.clear();
    }


    @Test
    void testReadFile_Success() throws IOException, InterruptedException {
        // Arrange
        byte[] expectedContent = Files.readAllBytes(Paths.get(config.getDocumentRoot() + File.separator + config.getDefaultPageFile()+ "." + config.getDefaultPageExtension()));

        // Act
        FileService fileService = new FileService(config,"/");
        fileService.start();
        fileService.join();

        byte[] result = fileService.getContent();

        // Assert
        assertArrayEquals(expectedContent, result);

    }

    @Test
    void testReadFile_FileNotFound() throws InterruptedException {
        // Arrange
        String nonExistentFilePath = "non_existent_file.txt";

        // Act
        FileService fileService = new FileService(config, nonExistentFilePath);
        fileService.start();
        fileService.join();

        byte[] result = fileService.getContent();

        // Assert
        assertEquals(0, result.length);
    }

    @Test
    void testReadFile_EmptyFile() throws IOException, InterruptedException {
        // Arrange
        Path tempFile = Files.createTempFile("emptyFile", ".txt");

        // Act
        FileService fileService = new FileService(config,tempFile.toString());
        fileService.start();
        fileService.join();

        byte[] result = fileService.getContent();

        // Assert
        assertEquals(0, result.length);

        // Cleanup
        Files.deleteIfExists(tempFile);
    }
}