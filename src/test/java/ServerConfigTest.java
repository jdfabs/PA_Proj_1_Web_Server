import config.ServerConfig;
import logging.LogLocation;
import logging.LogType;
import logging.LoggingTask;
import logging.SharedBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigTest {

    private ServerConfig config;

    @BeforeEach
    void setUp() {
        // Load config file for tests (from src/test/resources)
        config = new ServerConfig("src/test/java/resources/server.config");
    }

    @Test
    void testGetRoot() {
        assertEquals(System.getProperty("user.dir")+"/test_html/", config.getRoot());
    }

    @Test
    void testGetPort() {
        assertEquals(9090, config.getPort());
    }

    @Test
    void testGetDefaultPage() {
        assertEquals("index", config.getDefaultPageFile());
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
}
