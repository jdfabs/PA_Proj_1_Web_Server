import Cache.CacheManagerSingleton;
import config.ServerConfig;
import core.MainHTTPServerThread;
import logging.Logger;

public class Main {
    public static void main(String[] args) {

        ServerConfig config;
        config = new ServerConfig("server/server.config");

        Logger logger1 = new Logger(config);
        logger1.start();

        Logger logger2 = new Logger(config);
        logger2.start();

        CacheManagerSingleton.getInstance().setExpirationTime(config.getCacheExpirationTime());
        CacheManagerSingleton.getInstance().start();

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
