package core;

import config.ServerConfig;
import logging.*;
import utils.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Handles a single HTTP request from a client.
 * <p>
 * Reads the request, serves the requested file, and writes the HTTP response.
 */
public class RequestHandler implements LogProducer {
    private final BufferedReader in;
    private final OutputStream out;
    private final ServerConfig config;

    /**
     * Constructs a RequestHandler with necessary dependencies.
     *
     * @param br           input stream to read the client's request.
     * @param clientOutput output stream to send the response.
     */
    public RequestHandler(BufferedReader br, OutputStream clientOutput, ServerConfig serverConfig) {
        this.in = br;
        this.out = clientOutput;
        this.config = serverConfig;
    }

    /**
     * Processes the HTTP request and sends the appropriate response.
     */
    public void processRequest() {
        try {
            String request = readHttpRequest();
            logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "Request received: " + request));
            String route = parseRoute(request);
            if (route == null) {
                logMessage(new LoggingTask(LogType.Error, LogLocation.Console, "Invalid request received."));
                return;
            }

            FileService fileService = new FileService(config, route);
            RequestValidator requestValidator = new RequestValidator(request);
            HeaderBuilder headerBuilder = new HeaderBuilder();

            //ParBegin
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
                logMessage(new LoggingTask(LogType.Error, LogLocation.Console, "Invalid request"));
                sendErrorResponse(400, "Bad Request", header);
                return;
            }

            if (content.length == 0) {
                sendNotFoundResponse(header);
            } else {
                sendOkResponse(content, header);
            }
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, e.getMessage()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the HTTP request from the client.
     *
     * @return the HTTP request in string form.
     * @throws IOException if an I/O error occurs.
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
     * Extracts the route from the HTTP request.
     *
     * @param request the raw HTTP request.
     * @return the requested route, or null if invalid.
     */
    private String parseRoute(String request) {
        String[] tokens = request.split(" ");
        if (tokens.length < 2) {
            return null;
        }
        return tokens[1];
    }

    /**
     * Sends a 200 OK response with the requested content.
     *
     * @param content the file content to send.
     * @param headers additional HTTP headers as a string.
     * @throws IOException if an I/O error occurs.
     */
    private void sendOkResponse(byte[] content, String headers) throws IOException {
        logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "Response sent."));
        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write(headers.getBytes());
        out.write("\r\n".getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
    }

    /**
     * Sends a 404 Not Found response, optionally serving a custom 404.html page.
     *
     * @param headers additional HTTP headers as a string.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    private void sendNotFoundResponse(String headers) throws IOException, InterruptedException {
        // Create and start a FileService thread to read the custom 404 page.
        FileService fileService = new FileService(config , "/404.html");
        fileService.start();
        fileService.join();
        byte[] content = fileService.getContent();

        // Build the 404 response including the extra headers.
        String notFoundResponse = "HTTP/1.1 404 Not Found\r\n"
                + headers
                + "\r\n\r\n";
        out.write(notFoundResponse.getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
    }

    /**
     * Sends an error response with the specified HTTP status code and message.
     *
     * @param errorCode HTTP status code.
     * @param message   HTTP status message.
     * @param headers   additional HTTP headers as a string.
     * @throws IOException if an I/O error occurs.
     */
    private void sendErrorResponse(int errorCode, String message, String headers) throws IOException {
        // Build the error response including the extra headers.
        String response = "HTTP/1.1 " + errorCode + " " + message + "\r\n"
                + headers
                + "\r\n\r\n";
        out.write(response.getBytes());
        out.flush();
    }


}