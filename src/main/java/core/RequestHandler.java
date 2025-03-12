package core;

import logging.Logger;
import utils.FileService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Handles a single HTTP request from a client.
 * <p>
 * Reads the request, serves the requested file, and writes the HTTP response.
 */
public class RequestHandler {
    private final BufferedReader in;
    private final OutputStream out;
    private final String serverRoot;
    private final FileService fileService;
    private final Logger logger;


    /**
     * Constructs a RequestHandler with necessary dependencies.
     *
     * @param br           input stream to read the client's request.
     * @param clientOutput output stream to send the response.
     * @param serverRoot   root directory for serving files.
     * @param fileService  service to read files.
     * @param logger       logger for recording events.
     */
    public RequestHandler(BufferedReader br, OutputStream clientOutput, String serverRoot, FileService fileService, Logger logger) {
        this.in = br;
        this.out = clientOutput;
        this.serverRoot = serverRoot;
        this.fileService = fileService;
        this.logger = logger;
    }

    /**
     * Processes the HTTP request and sends the appropriate response.
     */
    public void processRequest() {
        try {
            String request = readHttpRequest();
            logger.info("Request received: " + request);
            String route = parseRoute(request);
            if (route == null) {
                logger.error("Invalid request received.");
                return;
            }

            byte[] content = fileService.readFile(serverRoot + route);
            if (content.length == 0) {
                sendNotFoundResponse();
            } else {
                sendOkResponse(content);
            }
        } catch (IOException e) {
            logger.error("Error processing request: " + e.getMessage());
            e.printStackTrace();
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
     * @throws IOException if an I/O error occurs.
     */
    private void sendOkResponse(byte[] content) throws IOException {
        logger.info("Response sent.");

        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write("Content-Type: text/html\r\n".getBytes());
        out.write("\r\n".getBytes());
        out.write(content);
        out.write("\r\n\r\n".getBytes());
        out.flush();
    }

    /**
     * Sends a 403 Not Found response, optionally serving a custom 404.html page.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void sendNotFoundResponse() throws IOException {
        byte[] content = fileService.readFile(serverRoot + "/403.html");

        String notFoundMessage = "HTTP/0.1 404 Not Found\r\n\r\n";
        out.write(notFoundMessage.getBytes());
        out.write(content);
        out.flush();
    }

}