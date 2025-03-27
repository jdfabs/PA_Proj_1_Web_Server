package utils;

import logging.LogProducer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A thread that builds standard HTTP response headers.
 * <p>
 * The {@code HeaderBuilder} prepares static headers such as {@code Content-Type}, {@code Server},
 * and the current {@code Date}, following basic HTTP formatting.
 * The result is stored internally and can be retrieved using {@link #getHeader()}.
 */
public class HeaderBuilder extends Thread implements LogProducer {
    /** A builder used to construct the HTTP response headers. */
    private final StringBuilder headerBuilder = new StringBuilder();

    /**
     * Constructs the HTTP headers when the thread is executed.
     * <p>
     * This includes:
     * <ul>
     *     <li>{@code Content-Type: text/html}</li>
     *     <li>{@code Server: pa-web-server}</li>
     *     <li>{@code Date: <current date>}</li>
     * </ul>
     */
    public void run() {
        headerBuilder.append("Content-Type: text/html\r\n");
        headerBuilder.append("Server: pa-web-server\r\n");
        headerBuilder.append("Date: ");
        headerBuilder.append(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date()));
        headerBuilder.append("\r\n");
    }

    /**
     * Returns the constructed HTTP headers as a string.
     *
     * @return the formatted HTTP header block
     */
    public String getHeader() {
        return headerBuilder.toString();
    }
}
