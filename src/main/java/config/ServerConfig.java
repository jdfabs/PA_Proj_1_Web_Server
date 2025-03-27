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
        try {
            return System.getProperty("user.dir") + "/" + properties.getProperty("server.root");
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server root is missing."));
            return "";
        }
    }

    public int getPort() {
        try {
            return Integer.parseInt(properties.getProperty("server.port"));
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server port Corrupt."));
            return 8080; //Default in case of corrupt conf
        }
    }

    public String getDefaultPageFile() {
        try {
            return properties.getProperty("server.default.page");
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server default page is missing."));
            return "index";
        }
    }

    public String getDefaultPageExtension() {
        try {
            return properties.getProperty("server.default.page.extension");
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server default page extension is missing."));
            return "html";
        }
    }

    public String getDocumentRoot() {
        try {
            return System.getProperty("user.dir") + properties.getProperty("server.document.root");
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server document root is missing."));
            return "/server/html";
        }
    }

    public String getPage404() {
        try {
            return properties.getProperty("server.page.404");
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server 404 page is missing."));
            return "404.html";
        }
    }

    public int getMaxRequests() {
        try {
            return Integer.parseInt(properties.getProperty("server.maximum.requests"));
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server max requests is corrupt."));
            return 5; //Default in case of corrupt conf
        }
    }

    public String getLogPath() {
        return properties.getProperty("server.logPath");
        try {
            return properties.getProperty("server.logPath");
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server log path is missing."));
            return "/logs";
        }
    }

    public String getLogFileName() {
        try {
            return properties.getProperty("server.logFileName");
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server log file name is missing."));
            return "loggingLogsLotsOfLogs";
        }
    }

    public Duration getCacheExpirationTime() {
        try {
            return Duration.ofSeconds(Integer.parseInt(properties.getProperty("server.cacheExpirationTime")));
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server cache expiration time is corrupt."));
            return Duration.ofSeconds(30); //Default of 30 secs in case of corrupt conf  file
        }
    }
}
