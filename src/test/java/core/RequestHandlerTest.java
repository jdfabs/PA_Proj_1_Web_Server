package core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import config.ServerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;


class RequestHandlerTest {

    private BufferedReader mockBufferedReader;
    private OutputStream mockOutputStream;
    private ServerConfig mockServerConfig;
    private RequestHandler mockRequestHandler;

    @BeforeEach
    void setUp() {
        mockBufferedReader = mock(BufferedReader.class);
        mockOutputStream = mock(OutputStream.class);
        mockServerConfig = mock(ServerConfig.class);

        requestHandler = new RequestHandler(mockBufferedReader, mockOutputStream, mockServerConfig, "120.0.0.1");

    }

    @Test
    public void readHttpRequestTest() throws IOException {
        String httpRequest = "GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";

        when(mockBufferedReader.readLine()).thenReturn(httpRequest)
                .thenReturn("GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n");

        String result = RequestHandler.readHttpRequest();
        assert(result.equals("GET /index.html HTTP/1.1\r\nHost: localhost\r\n"));
    }

    @Test
    void RequestHandlerTest(){
    }

    @Test
    void sendOkResponseTest() throws IOException {
    }

    @Test
    void SendNotFoundResponseTest() throws IOException {

    }

    @Test
    void processInvalidRequestTest() throws IOException {

    }



}