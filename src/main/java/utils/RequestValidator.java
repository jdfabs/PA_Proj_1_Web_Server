package utils;

import java.util.Random;

public class RequestValidator extends Thread {
    private String request;
    private boolean isValidRequest;

    public RequestValidator(String request) {
        this.request = request;
    }

    @Override
    public void run() {
        isValidRequest = request.startsWith("GET") && request.split(" ").length >= 2;

        //Faking long tasks
        System.out.println("Computing extremely hard validation request bip bop");
        try {
            Thread.sleep(new Random().nextInt(500,9999));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean getIsValidRequest() {
        return isValidRequest ;
    }
}
