package core;

import config.ServerConfig;
import logging.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple multithreaded HTTP server that listens on a specified port and serves files from a configured root directory.
 * <p>
 * It uses a {@link ThreadPool} to manage concurrent client connections and delegates request handling
 * to {@link RequestHandler}. Configuration settings such as port number and document root are loaded
 * via {@link ServerConfig}.
 */
public class MainHTTPServerThread extends Thread implements LogProducer {
    /** Configuration for the server (port, root directory, etc.). */
    private final ServerConfig serverConfig;
    /** Pool of worker threads used to handle incoming client requests concurrently. */
    private final ThreadPool threadPool;

    /**
     * Constructs the HTTP server thread using the specified server configuration.
     *
     * @param config the server configuration containing parameters such as port number and document root
     */
    public MainHTTPServerThread(ServerConfig config) {
        this.serverConfig = config;
        this.threadPool = new ThreadPool(config.getMaxRequests());
    }

    /**
     * Starts the HTTP server.
     * <p>
     * Opens a {@link ServerSocket} on the configured port and listens for incoming client connections.
     * Each new connection is passed to the {@link #handleClient(Socket)} method via a thread in the thread pool.
     */
    @Override
    public void run() {
        logMessage(new LoggingTask(LogType.Info,LogLocation.ConsoleOut,"MainHTTPServerThread has started!"));

        try (ServerSocket serverSocket = new ServerSocket(serverConfig.getPort())) {
            logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Server started on port: " + serverConfig.getPort()));
            logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Server root: " + serverConfig.getDocumentRoot()));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "New client connected: " + clientSocket.getInetAddress()));

                threadPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server error: " + e.getMessage()));
        }
    }

    /**
     * Handles an individual client connection.
     * <p>
     * Wraps the socket's input and output streams in a {@link BufferedReader} and {@link OutputStream},
     * then creates a {@link RequestHandler} to process the client's HTTP request.
     *
     * @param clientSocket the socket connected to the client
     */
    private void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream clientOutput = socket.getOutputStream()) {

            RequestHandler requestHandler = new RequestHandler(br, clientOutput, serverConfig, clientSocket.getInetAddress().getHostAddress());
            requestHandler.processRequest();
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Error handling client request: " + e.getMessage()));
        }
    }
}