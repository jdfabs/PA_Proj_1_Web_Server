package core;

import config.ServerConfig;
import logging.*;
import utils.FileService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple HTTP server that listens on a specified port.
 * It serves files from a predefined server root directory.
 */
public class MainHTTPServerThread extends Thread implements LogProducer {

    private static String documentRoot = System.getProperty("user.dir"); // Define by user
    private final int port;

    /**
     * Constructor to initialize the HTTP server thread with the specified configuration, file service, and logger.
     *
     * @param config the server configuration containing port and root directory information.
     */
    public MainHTTPServerThread(ServerConfig config) {
        this.port = config.getPort();
        documentRoot += config.getDocumentRoot();
    }


    /**
     * Starts the HTTP server.
     * This method initializes a {@code ServerSocket} on the {@link ServerConfig} port and listens new connections.
     * On a new connection it's logged and sent to {@code handleClient}.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "Server started on port: " + port));
            logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "Server root: " + documentRoot));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "New client connected: " + clientSocket.getInetAddress()));

                //THREAD POOL SHOULD BE IMPLEMENTED HERE, added to test File monitor...
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, "Server error: " + e.getMessage()));
            e.printStackTrace();
        }
    }


    /**
     * This method wraps the client socket's input and output streams in a {@link BufferedReader} and {@link OutputStream}
     * respectively, then creates a {@link RequestHandler} to process the HTTP request.
     *
     * @param clientSocket the client socket.
     */
    private void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream clientOutput = socket.getOutputStream()) {

            RequestHandler requestHandler = new RequestHandler(br, clientOutput, documentRoot);
            requestHandler.processRequest();
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, "Error handling client request: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}