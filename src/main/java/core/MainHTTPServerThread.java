package core;

import config.ServerConfig;
import logging.Logger;
import utils.FileService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple HTTP server that listens on a specified port.
 * It serves files from a predefined server root directory.
 */
public class MainHTTPServerThread extends Thread {

    private static String SERVER_ROOT = System.getProperty("user.dir"); // Define by user
    private final int port;
    private final Logger logger;

    /**
     * Constructor to initialize the HTTP server thread with the specified configuration, file service, and logger.
     *
     * @param config      the server configuration containing port and root directory information.
     * @param logger      the logger used for logging server events and errors.
     */
    public MainHTTPServerThread(ServerConfig config, Logger logger) {
        this.port = config.getPort();
        SERVER_ROOT += config.getRoot();
        this.logger = logger;
    }


    /**
     * Starts the HTTP server.
     * This method initializes a {@code ServerSocket} on the {@link ServerConfig} port and listens new connections.
     * On a new connection it's logged and sent to {@code handleClient}.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started on port: " + port);
            logger.info("Server root: " + SERVER_ROOT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("New client connected: " + clientSocket.getInetAddress());

                //THREAD POOL SHOULD BE IMPLEMENTED HERE, added to test File monitor...
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            logger.error("Server error: " + e.getMessage());
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

            RequestHandler requestHandler = new RequestHandler(br, clientOutput, SERVER_ROOT );
            requestHandler.processRequest();
        } catch (IOException e) {
            logger.error("Error handling client request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}