import config.ServerConfig;
import core.MainHTTPServerThread;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.Socket;

public class MainHTTPServerThreadTest {

    private MainHTTPServerThread serverThread;
    private int testPort;

    @BeforeEach
    public void setUp() throws Exception {
        ServerConfig config = new ServerConfig("src/test/java/resources/server.config");

        serverThread = new MainHTTPServerThread(config);
        serverThread.setDaemon(true);
        serverThread.start();

        testPort = config.getPort();

        Thread.sleep(500);
    }

    @AfterEach
    public void cleanup() {
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    @Test
    public void testServerAcceptsConnection() {
        try (Socket socket = new Socket("localhost", testPort)) {
            assertTrue(socket.isConnected(), "Socket should be connected to the server.");
        } catch (IOException e) {
            fail("Connection to server failed: " + e.getMessage());
        }
    }

    @Test
    public void testMultipleConnections() {
        final int connectionCount = 5;
        for (int i = 0; i < connectionCount; i++) {
            try (Socket socket = new Socket("localhost", testPort)) {
                assertTrue(socket.isConnected(), "Socket " + i + " should be connected.");
            } catch (IOException e) {
                fail("Connection " + i + " failed: " + e.getMessage());
            }
        }
    }
}
