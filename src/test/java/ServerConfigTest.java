import config.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigTest {

    private ServerConfig config;
    private ServerConfig emptyConfig;

    @BeforeEach
    void setUp() {
        // Load config file for tests (from src/test/resources)
        config = new ServerConfig("src/test/java/resources/server.config");
        emptyConfig = new ServerConfig("");
    }

    @Test
    void testGetRoot() {
        assertEquals(System.getProperty("user.dir")+"/test_html/", config.getRoot());
        assertEquals("", emptyConfig.getRoot());
    }

    @Test
    void testGetPort() {
        assertEquals(9090, config.getPort());
        assertEquals(8080, emptyConfig.getPort());
    }

    @Test
    void testGetDefaultPage() {
        assertEquals("index", config.getDefaultPageFile());
        assertEquals("index", emptyConfig.getDefaultPageFile());
    }

    @Test
    void testGetDefaultPageExtension() {
        assertEquals("html", config.getDefaultPageExtension());
        assertEquals("html", emptyConfig.getDefaultPageExtension());
    }

    @Test
    void testGetPage404() {
        assertEquals("test_404.html", config.getPage404());
        assertEquals("404.html", emptyConfig.getPage404());
    }

    @Test
    void testGetMaximumRequests() {
        assertEquals(10, config.getMaxRequests());
        assertEquals(5, emptyConfig.getMaxRequests());
    }

    @Test
    void testGetCacheExpiration() {
        assertEquals(Duration.ofSeconds(10),config.getCacheExpirationTime());
        assertEquals(Duration.ofSeconds(30),emptyConfig.getCacheExpirationTime());
    }
}
