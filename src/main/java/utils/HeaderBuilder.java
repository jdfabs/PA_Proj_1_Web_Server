package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class HeaderBuilder extends Thread {
    private StringBuilder headerBuilder = new StringBuilder();

    public void run() {
        headerBuilder.append("Content-Type: text/html\r\n")
                .append("Server: pa-web-server\r\n")
                .append("Date: " + new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(new Date()) + "\r\n");

        //Faking long tasks
        System.out.println("Computing extremely hard header bip bop");
        try {
            Thread.sleep(new Random().nextInt(500,9999));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        //Finish Faking long tasks
    }

    public String getHeader() {
        return headerBuilder.toString();
    }
}
