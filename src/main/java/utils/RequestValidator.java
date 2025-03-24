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
    }

    public boolean getIsValidRequest() {
        return isValidRequest;
    }
}
