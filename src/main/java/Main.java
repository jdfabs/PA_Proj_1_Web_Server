import config.ServerConfig;
import core.MainHTTPServerThread;
import logging.Logger;

public class Main {
    public static void main(String[] args) {

        ServerConfig config = null;
        Logger logger = new Logger();
        logger.start();

        config = new ServerConfig("server/server.config");

        if (config.getRoot() == null) {
            //Failed to load config
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
