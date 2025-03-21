package utils;

import logging.LogLocation;
import logging.LogProducer;
import logging.LogType;
import logging.LoggingTask;

import java.util.Random;

public class RequestValidator extends Thread implements LogProducer {
    private final String request;
    private boolean isValidRequest;

    public RequestValidator(String request) {
        this.request = request;
    }

    @Override
    public void run() {
        isValidRequest = request.startsWith("GET") && request.split(" ").length >= 2;

        //Faking long tasks
        logMessage(new LoggingTask(LogType.Info, LogLocation.Console, "Computing extremely hard validation request bip bop"));
        try {
            Thread.sleep(new Random().nextInt(500, 9999));
        } catch (InterruptedException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, e.getMessage()));
        }
        //Finish Faking long tasks
    }

    public boolean getIsValidRequest() {
        return isValidRequest;
    }
}
