import config.ServerConfig;
import core.MainHTTPServerThread;
import logging.Logger;
import utils.FileService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        ServerConfig config = null;
        Logger logger = new Logger();

        try {
            config = new ServerConfig("server/server.config");

        } catch (IOException e) {
            logger.error("Error loading server config: " + e.getMessage());
            System.exit(1);
        }

        MainHTTPServerThread s = new MainHTTPServerThread(config, logger);
        s.start();
        try {
            s.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
