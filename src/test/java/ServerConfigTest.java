import config.ServerConfig;
import logging.LogLocation;
import logging.LogType;
import logging.LoggingTask;
import logging.SharedBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigTest {

    private ServerConfig config;

    @BeforeEach
    void setUp() {
        // Load config file for tests (from src/test/resources)
        config = new ServerConfig("src/test/java/resources/server.config");
        SharedBuffer.buffer.clear();
    }

    @Test
    void testGetRoot() {
        assertEquals("\"test_html/\"", config.getRoot());
    }

    @Test
    void testGetPort() {
        assertEquals(9090, config.getPort());
    }

    @Test
    void testGetDefaultPage() {
        assertEquals("test_index", config.getDefaultPageFile());
    }

    @Test
    void testGetDefaultPageExtension() {
        assertEquals("html", config.getDefaultPageExtension());
    }

    @Test
    void testGetPage404() {
        assertEquals("test_404.html", config.getPage404());
    }

    @Test
    void testGetMaximumRequests() {
        assertEquals(10, config.getMaxRequests());
    }

    @Test
    void testInvalidFileThrowsIOException() throws InterruptedException { //implementation changed with Logger - now logs the message! :D

        new ServerConfig("invalid/path/to/config.file");

        TimeUnit.MILLISECONDS.sleep(100);

        LoggingTask nextLog = SharedBuffer.buffer.remove();
        assertTrue(nextLog.getMessage().contains("Error loading server config: "));
        assertEquals(LogType.Error, nextLog.getType());
        assertEquals(LogLocation.Console, nextLog.getLocation());

    }
}
