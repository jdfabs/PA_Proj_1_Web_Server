package utils;

import logging.LogProducer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderBuilder extends Thread implements LogProducer {
    private final StringBuilder headerBuilder = new StringBuilder();

    public void run() {
        headerBuilder.append("Content-Type: text/html\r\n");
        headerBuilder.append("Server: pa-web-server\r\n");
        headerBuilder.append("Date: ");
        headerBuilder.append(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date()));
        headerBuilder.append("\r\n");
    }

    public String getHeader() {
        return headerBuilder.toString();
    }
}
