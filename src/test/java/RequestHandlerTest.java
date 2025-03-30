import config.ServerConfig;
import core.RequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileService;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RequestHandlerTest {

    private FileService fileService;
    private FileService fileService404;
    private ByteArrayOutputStream clientOutput;
    private ServerConfig config;

    @BeforeEach
    void setup() {
        fileService = mock(FileService.class);  // Mock FileService
        fileService404 = mock(FileService.class);
        clientOutput = new ByteArrayOutputStream(); // Output stream to capture responses
        config = new ServerConfig("src/test/java/resources/server.config");
    }

    @Test
    void shouldRespond404WhenFileDoesNotExist() {
        // Arrange
        String httpRequest = "GET /missing.html HTTP/1.1\r\n\r\n";

        // Act
        when(fileService.getContent()).thenReturn(new byte[0]);
        when(fileService404.getContent()).thenReturn("<h1>404 Not Found</h1>".getBytes());

        BufferedReader input = new BufferedReader(new StringReader(httpRequest));
        RequestHandler handler = new RequestHandler(input, clientOutput, config,"127.0.0.1");

        handler.processRequest();

        // Assert
        String response = clientOutput.toString();
        assertTrue(response.contains("404 Not Found"));
        assertTrue(response.contains("404"));
    }

    @Test
    void shouldRespond200WhenFileExists() throws Exception {
        // Arrange
        String httpRequest = "GET /index.html HTTP/1.1\r\n\r\n";

        BufferedReader input = new BufferedReader(new StringReader(httpRequest));
        RequestHandler handler = new RequestHandler(input, clientOutput, config, "127.0.0.1");

        // Act
        handler.processRequest();

        // Assert
        String response = clientOutput.toString();
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Message must contain status 200 OK");
        assertTrue(response.contains("Content-Type: text/html"), "Message must contain header Content-Type");
        assertTrue(response.contains("Server: pa-web-server"), "Message must contain header Server");
        assertTrue(response.contains("Date:"), "Message must contain header Date");
        assertTrue(response.contains("<!DOCTYPE html>"), "Message must contain DOCTYPE html");
        assertTrue(response.contains("<html lang=\"en-GB\">"), "Message must contain html lang en-GB");
        assertTrue(response.contains("<head>"), "Message must contain head");
        assertTrue(response.contains("<title>HOME</title>"), "Message must contain title HOME");
        assertTrue(response.contains("</head>"), "Message must contain /head");
        assertTrue(response.contains("<body>"), "Message must contain body");
        assertTrue(response.contains("<h1>Welcome to PA Website</h1>"), "Message must contain Welcome to PA Website");
        assertTrue(response.contains("<p>This is the index.html file</p>"), "Message must contain This is the index.html file");
        assertTrue(response.contains("</body>"), "Message must contain /body");
        assertTrue(response.contains("</html>"), "Message must contain /html");
    }
}