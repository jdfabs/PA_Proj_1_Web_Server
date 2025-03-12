import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServerConfigTest {

    private ServerConfig config;

    @BeforeEach
    void setUp() throws IOException {
        // Load config file for tests (from src/test/resources)
        config = new ServerConfig("src/test/java/resources/server.config");
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
    void testInvalidFileThrowsIOException() {
        assertThrows(IOException.class, () -> {
            new ServerConfig("invalid/path/to/config.file");
        });
    }
}
