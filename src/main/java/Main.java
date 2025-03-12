import config.ServerConfig;
import core.MainHTTPServerThread;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        ServerConfig config = null;

        try {
            config = new ServerConfig("server/server.config");

        } catch (IOException e) {
            System.err.println("Error loading server configuration: " + e.getMessage());
            System.exit(1);
        }

        MainHTTPServerThread s = new MainHTTPServerThread(config);
        s.start();
        try {
            s.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
