package config;

import logging.*;

import java.time.Duration;
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;


public class ServerConfig implements LogProducer {
    private final Properties properties = new Properties();

    public ServerConfig(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Error loading server config: " + e.getMessage()));
        }

    }

    public String getRoot() {
        return  System.getProperty("user.dir")+"/" + properties.getProperty("server.root");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }

    public String getDefaultPageFile() {
        return properties.getProperty("server.default.page");
    }

    public String getDefaultPageExtension() {
        return properties.getProperty("server.default.page.extension");
    }

    public String getDocumentRoot() {
        return System.getProperty("user.dir") + properties.getProperty("server.document.root");
    }

    public String getPage404() {
        return properties.getProperty("server.page.404");
    }

    public int getMaxRequests() {
        try {

            return Integer.parseInt(properties.getProperty("server.maximum.requests"));
        }
        catch (Exception e) {
            return 5; //Default in case of corrupt conf
        }
    }

    public String getLogPath() {
        return properties.getProperty("server.logPath");
    }

    public String getLogFileName() {
        return properties.getProperty("server.logFileName");
    }

    public Duration getCacheExpirationTime() {
        try{
            return Duration.ofSeconds(Integer.parseInt(properties.getProperty("server.cacheExpirationTime")));
        }
        catch(Exception e){
            return Duration.ofSeconds(30); //Default of 30 secs in case of corrupt conf  file
        }
    }
}
