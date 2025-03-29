package core;

import static org.junit.jupiter.api.Assertions.*;

import Cache.CacheEntry;
import config.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.plugins.DoNotMockEnforcer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainHTTPServerThreadTest {
    private ServerConfig mockServerConfig;
    private ThreadPool mockThreadPool;
    private ServerSocket mockServerSocket;
    private MainHTTPServerThread ServerThread;
    private Socket mockSocket;

    @BeforeEach
    public void setUp() {
        mockServerConfig = mock(ServerConfig.class);
        mockThreadPool = mock(ThreadPool.class);
        mockServerSocket = mock(ServerSocket.class);
        mockSocket = mock(Socket.class);

        when(mockServerConfig.getPort()).thenReturn(8080);
        when(mockServerConfig.getMaxRequests()).thenReturn(10);
        when(mockServerConfig.getDocumentRoot()).thenReturn("/");

        ServerThread = new MainHTTPServerThread(mockServerConfig);
    }

    @Test
    public void runServerTest() throws IOException {

        when(mockServerSocket.accept()).thenReturn(mockSocket);

        ServerThread.run();
        verify(mockServerSocket, times(1)).accept();
        verify(mockThreadPool, times(1)).execute(Mockito.any(Runnable.class));
    }

    /*
    Simulates the processing of a request, and its handling
     */
    @Test
    public void runServerSocketTest() throws IOException {
        BufferedReader mockBufferedReader = mock(BufferedReader.class);
        OutputStream mockOutputStream = mock(OutputStream.class);

        RequestHandler mockRequestHandler = mock(RequestHandler.class);
        when(mockRequestHandler.processRequest()).thenReturn(null);   // ??

        ServerThread.handleClient(mockSocket);
        verify(mockServerSocket, times(1)).processRequest();


    }

}