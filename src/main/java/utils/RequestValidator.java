package utils;

import logging.LogProducer;

/**
 * A thread that validates the structure of an incoming HTTP request.
 * <p>
 * The {@code RequestValidator} checks whether the request is a well-formed HTTP GET request.
 * The result of the validation can be retrieved using {@link #getIsValidRequest()}.
 * <p>
 * This class is designed to be used in parallel with other request-processing components
 * as part of a parbegin-parend concurrency pattern.
 */
public class RequestValidator extends Thread implements LogProducer {
    /** The raw HTTP request string. */
    private final String request;
    /** Whether the request is valid (set during execution). */
    private boolean isValidRequest;

    /**
     * Constructs a {@code RequestValidator} with the specified HTTP request.
     *
     * @param request the raw HTTP request string to validate
     */
    public RequestValidator(String request) {
        this.request = request;
    }

    /**
     * Validates the request format when the thread is executed.
     * <p>
     * The request is considered valid if it starts with {@code GET}
     * and contains at least two tokens (method and route).
     */
    @Override
    public void run() {
        isValidRequest = request.startsWith("GET") && request.split(" ").length >= 2;
    }

    /**
     * Returns whether the request passed validation.
     *
     * @return {@code true} if the request is valid; {@code false} otherwise
     */
    public boolean getIsValidRequest() {
        return isValidRequest;
    }
}
