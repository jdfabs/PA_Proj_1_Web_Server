import logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileMonitor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileMonitorTest {

    private FileMonitor fileMonitor;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = mock(Logger.class); // Mock the logger for verifying error logging
        fileMonitor = new FileMonitor(logger);
    }

    @Test
    void testLockUnlockFile_NoException() {
        String fileName = "test.html";

        // Should lock and unlock without any exception
        assertDoesNotThrow(() -> {
            fileMonitor.lockFile(fileName);
            fileMonitor.unlockFile(fileName);
        });
    }

    @Test
    void testUnlockFile_WithoutLock_LogsError() {
        String fileName = "unlockedFile.html";

        // Attempting to unlock a file that hasn't been locked should log an error
        fileMonitor.unlockFile(fileName);

        verify(logger, times(1)).error(contains("Lock for file \"unlockedFile.html\" was not found"));
    }
}