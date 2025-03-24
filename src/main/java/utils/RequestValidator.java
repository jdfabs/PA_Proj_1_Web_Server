package utils;

import logging.LogProducer;

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
