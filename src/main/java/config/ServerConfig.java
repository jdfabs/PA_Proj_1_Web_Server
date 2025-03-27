package config;

import logging.*;

import java.util.Properties;
import java.io.FileInputStream;


public class ServerConfig implements LogProducer {
    private Properties properties = new Properties();

    public ServerConfig(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            properties.load(fis);
            fis.close();
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.Console, "Error loading server config: " + e.getMessage()));
        }

    }

    public String getRoot() {
        return properties.getProperty("server.root");
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
        return properties.getProperty("server.document.root");
    }

    public String getPage404() {
        return properties.getProperty("server.page.404");
    }

    public int getMaxRequests() {
        return Integer.parseInt(properties.getProperty("server.maximum.requests"));
    }


}
