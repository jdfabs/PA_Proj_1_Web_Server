import logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Test
    void testReadFile_Success() throws IOException, InterruptedException {
        // Arrange
        Path tempFile = Files.createTempFile("testFile", ".txt");
        String expectedContent = "Hello, World!";
        Files.write(tempFile, expectedContent.getBytes());

        // Act
        FileService fileService = new FileService(tempFile.toString());
        fileService.start();
        fileService.join();

        byte[] result = fileService.getContent();

        // Assert
        assertArrayEquals(expectedContent.getBytes(), result);

        // Cleanup
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testReadFile_FileNotFound() throws InterruptedException {
        // Arrange
        String nonExistentFilePath = "non_existent_file.txt";

        // Act
        FileService fileService = new FileService(nonExistentFilePath.toString());
        fileService.start();
        fileService.join();

        byte[] result = fileService.getContent();

        // Assert
        assertEquals(0, result.length);
        //verify(mockLogger, times(1)).error(contains("Error reading file"));
    }

    @Test
    void testReadFile_EmptyFile() throws IOException, InterruptedException {
        // Arrange
        Path tempFile = Files.createTempFile("emptyFile", ".txt");

        // Act
        FileService fileService = new FileService(tempFile.toString());
        fileService.start();
        fileService.join();

        byte[] result = fileService.getContent();

        // Assert
        assertEquals(0, result.length);

        // Cleanup
        Files.deleteIfExists(tempFile);
    }
}