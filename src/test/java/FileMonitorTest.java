import logging.LogLocation;
import logging.LogType;
import logging.LoggingTask;
import logging.SharedBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileMonitor;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FileMonitorTest {

    private FileMonitor fileMonitor;

    @BeforeEach
    void setUp() {
        SharedBuffer.buffer.clear();
        fileMonitor = new FileMonitor();
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
    void testUnlockFile_WithoutLock_LogsError() throws InterruptedException {
        String fileName = "unlockedFile.html";

        // Attempting to unlock a file that hasn't been locked should log an error
        fileMonitor.unlockFile(fileName);

        TimeUnit.MILLISECONDS.sleep(100); //Wait message

        LoggingTask nextLog = SharedBuffer.buffer.remove();
        assertTrue(nextLog.getMessage().contains("Lock for file \"" + fileName + "\" was not found"));
        assertEquals(LogType.Error, nextLog.getType());
        assertEquals(LogLocation.Console, nextLog.getLocation());
    }
}