package core;

import config.ServerConfig;
import logging.*;
import utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles a single HTTP request from a connected client.
 * <p>
 * This class reads an incoming HTTP request, validates it, attempts to serve the requested
 * file from the server's root, and writes the corresponding HTTP response back to the client.
 * It supports a parbegin-parend approach by launching three concurrent tasks:
 * <ul>
 *     <li>Reading the requested file (with locking and caching)</li>
 *     <li>Validating the HTTP request</li>
 *     <li>Generating HTTP headers</li>
 * </ul>
 */
public class RequestHandler implements LogProducer {
    /** Input stream to read the client's HTTP request. */
    private final BufferedReader in;
    /** Output stream to send the HTTP response. */
    private final OutputStream out;
    /** Server configuration object providing paths, defaults, etc. */
    private final ServerConfig config;
    /** Origin IP address of the client. */
    private final String origin;

     /**
     * Constructs a {@code RequestHandler}.
     *
     * @param br             input stream to read the client's request
     * @param clientOutput   output stream to send the HTTP response
     * @param serverConfig   configuration of the server
     * @param clientAddress  the IP address of the connected client
     */
    public RequestHandler(BufferedReader br, OutputStream clientOutput, ServerConfig serverConfig, String clientAddress) {
        this.in = br;
        this.out = clientOutput;
        this.config = serverConfig;
        this.origin = clientAddress;
    }

    /**
     * Processes the client's HTTP request and sends the appropriate HTTP response.
     * <p>
     * It performs the following in parallel:
     * <ul>
     *     <li>Reads the file (with semaphore + cache logic)</li>
     *     <li>Validates the HTTP request format</li>
     *     <li>Builds the HTTP response headers</li>
     * </ul>
     * After synchronization (join), it determines the validity and serves a 200, 400, or 404 response.
     */
    public void processRequest() {
        try {
            String request = readHttpRequest();
            String route = parseRoute(request);
            if (route == null) {
                logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Invalid request received."));
                return;
            }

            //ParBegin
            FileService fileService = new FileService(config, route);
            RequestValidator requestValidator = new RequestValidator(request);
            HeaderBuilder headerBuilder = new HeaderBuilder();

            fileService.start();
            requestValidator.start();
            headerBuilder.start();

            //ParEnd
            fileService.join();
            requestValidator.join();
            headerBuilder.join();


            byte[] content = fileService.getContent();
            boolean isValid = requestValidator.getIsValidRequest();
            String header = headerBuilder.getHeader();

            if (!isValid) {
                logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Invalid request"));
                sendErrorResponse(header);
                logMessage(new LoggingTask(LogType.Request,LogLocation.File,request+ " 400 " +  origin));
                return;
            }

            if (content.length == 0) {
                sendNotFoundResponse(header);
                logMessage(new LoggingTask(LogType.Request,LogLocation.File,request+ " 404 " +  origin));
            } else {
                sendOkResponse(content, header);
                logMessage(new LoggingTask(LogType.Request,LogLocation.File,request+ " 200 " +  origin));
            }
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, e.getMessage()));
        }
    }

    /**
     * Reads the full HTTP request header from the client connection.
     *
     * @return the raw HTTP request as a string
     * @throws IOException if an I/O error occurs
     */
    private String readHttpRequest() throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null && !line.isBlank()) {
            builder.append(line).append("\r\n");
        }
        return builder.toString();
    }

    /**
     * Parses the request route from the HTTP request line.
     *
     * @param request the full HTTP request string
     * @return the path requested (e.g., "/index.html"), or {@code null} if the request too short (malformed).
     */
    private String parseRoute(String request) {
        String[] tokens = request.split(" ");
        if (tokens.length < 2) {
            return null;
        }
        return tokens[1];
    }

    /**
     * Sends a 200 OK response with the specified headers and content.
     *
     * @param content the body of the HTTP response
     * @param headers additional headers to include in the response
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void sendOkResponse(byte[] content, String headers) throws IOException {
        logMessage(new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Response sent."));
        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write(headers.getBytes());
        out.write("\r\n".getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
    }

    /**
     * Sends a 404 Not Found response, attempting to serve a custom 404 page if available.
     *
     * @param headers additional HTTP headers as a string
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted while waiting for file read completion
     */
    private void sendNotFoundResponse(String headers) throws IOException, InterruptedException {
        FileService fileService = new FileService(config , "/404.html");
        fileService.start();
        fileService.join();
        byte[] content = fileService.getContent();

        String notFoundResponse = "HTTP/1.1 404 Not Found\r\n"
                + headers
                + "\r\n\r\n";
        out.write(notFoundResponse.getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
    }

    /**
     * Sends a general error response with the specified HTTP status and headers.
     *
     * @param headers additional headers to include in the response
     * @throws IOException if an I/O error occurs
     */
    private void sendErrorResponse(String headers) throws IOException {
        String response = "HTTP/1.1 400 Bad Request\r\n"
                + headers
                + "\r\n\r\n";
        out.write(response.getBytes());
        out.flush();
    }


}