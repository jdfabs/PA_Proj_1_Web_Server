import config.ServerConfig;
import logging.SharedBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        String expectedContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en-GB\">\n" +
                "<head>\n" +
                "    <title>HOME</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>Welcome to PA Website</h1>\n" +
                "<p>This is the index.html file</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        // Act
        FileService fileService = new FileService(config,"/");
        fileService.start();
        fileService.join();

        byte[] result = fileService.getContent();

        // Assert
        assertArrayEquals(expectedContent.getBytes(), result);

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