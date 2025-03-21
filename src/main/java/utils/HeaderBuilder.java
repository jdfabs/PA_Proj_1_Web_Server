package utils;

import logging.LogLocation;
import logging.LogProducer;
import logging.LogType;
import logging.LoggingTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class HeaderBuilder extends Thread implements LogProducer {
    private final StringBuilder headerBuilder = new StringBuilder();

    public void run() {
        headerBuilder.append("Content-Type: text/html\r\n");
        headerBuilder.append("Server: pa-web-server\r\n");
        headerBuilder.append("Date: ");
        headerBuilder.append(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date()));
        headerBuilder.append("\r\n");

        //Faking long tasks
        logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "Computing extremely hard header bip bop"));
        try {
            Thread.sleep(new Random().nextInt(500, 9999));
        } catch (InterruptedException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, e.getMessage()));
        }
        //Finish Faking long tasks
    }

    public String getHeader() {
        return headerBuilder.toString();
    }
}
