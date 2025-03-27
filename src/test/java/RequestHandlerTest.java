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
}