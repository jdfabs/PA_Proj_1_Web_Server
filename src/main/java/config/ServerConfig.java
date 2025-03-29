package config;

import logging.*;

import java.time.Duration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * Handles loading and accessing server settings from the config file.
 * <p>
 * This class is responsible for retrieving server parameters such as port, document root,
 * default pages, logging preferences, maximum concurrent requests, and cache expiration time.
 * It implements {@link LogProducer} to allow logging configuration-related issues.
 */
public class ServerConfig implements LogProducer {
    /**
     * Properties object that holds all key-value pairs loaded from the configuration file.
     */
    private final Properties properties = new Properties();

    /**
     * Constructs a {@code ServerConfig} and loads configuration from the given file path.
     * Logs an error if the file cannot be loaded.
     *
     * @param filePath the path to the server configuration file
     */
    public ServerConfig(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Error loading server config: " + e.getMessage()));
        }

    }

    /**
     * Returns the root directory where the server is located (working directory + server.root).
     * If the configuration is missing, defaults to "".
     *
     * @return absolute path to the server root directory
     */
    public String getRoot() {
        try {
            String root = properties.getProperty("server.root");
            if (root == null) throw new NoSuchFieldException();
            return System.getProperty("user.dir") + "/" + root;
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server root is missing."));
            return "";
        }
    }

    /**
     * Returns the port number the server should listen on.
     * If the configuration is invalid, missing or corrupt, defaults to 8080.
     *
     * @return the configured port number
     */
    public int getPort() {
        try {
            return Integer.parseInt(properties.getProperty("server.port"));
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server port Corrupt."));
            return 8080; //Default in case of corrupt conf
        }
    }

    /**
     * Returns the default file name to serve when a directory is requested.
     * If the configuration is missing, defaults to "index".
     *
     * @return the name of the default page file
     */
    public String getDefaultPageFile() {
        try {
            String defaultPage = properties.getProperty("server.default.page");
            if (defaultPage == null) throw new NoSuchFieldException();
            return defaultPage;
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server default page is missing."));
            return "index";
        }
    }

    /**
     * Returns the default file extension for pages.
     * If the configuration is missing, defaults to "html".
     *
     * @return the file extension for default pages
     */
    public String getDefaultPageExtension() {
        try {
            String extension = properties.getProperty("server.default.page.extension");
            if (extension == null) throw new NoSuchFieldException();
            return extension;
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server default page extension is missing."));
            return "html";
        }
    }

    /**
     * Returns the document root directory from which content should be served.
     * If the configuration is missing, defaults to "/server/html".
     *
     * @return the absolute path to the document root
     */
    public String getDocumentRoot() {
        try {
            String docRoot = properties.getProperty("server.document.root");
            if (docRoot == null) throw new NoSuchFieldException();
            return System.getProperty("user.dir") + docRoot;
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server document root is missing."));
            return "/server/html";
        }
    }

    /**
     * Returns the relative path to the custom 404 error page.
     * If the configuration is missing, defaults to "404.html".
     *
     * @return the configured 404 error page path
     */
    public String getPage404() {
        try {
            String page404 = properties.getProperty("server.page.404");
            if (page404 == null) throw new NoSuchFieldException();
            return page404;
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server 404 page is missing."));
            return "404.html";
        }
    }

    /**
     * Returns the maximum number of concurrent requests the server should handle.
     * If the configuration is invalid or missing, defaults to 5.
     *
     * @return the maximum number of requests
     */
    public int getMaxRequests() {
        try {
            return Integer.parseInt(properties.getProperty("server.maximum.requests"));
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server max requests is corrupt."));
            return 5; //Default in case of corrupt conf
        }
    }

    /**
     * Returns the directory where server logs should be stored.
     * If the configuration is missing, defaults to "/logs".
     *
     * @return the path to the log directory
     */
    public String getLogPath() {
        try {
            String path = properties.getProperty("server.logPath");
            if (path == null) throw new NoSuchFieldException();
            return path;
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server log path is missing."));
            return "/logs";
        }
    }

    /**
     * Returns the name of the log file where request logs will be written.
     * If the configuration is missing, defaults to "loggingLogsLotsOfLogs".
     *
     * @return the name of the log file
     */
    public String getLogFileName() {
        try {
            String fileName = properties.getProperty("server.logFileName");
            if (fileName == null) throw new NoSuchFieldException();
            return fileName;
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server log file name is missing."));
            return "loggingLogsLotsOfLogs";
        }
    }

    /**
     * Returns the configured cache expiration duration.
     * If the configuration is invalid or missing, defaults to 30 seconds.
     *
     * @return the cache expiration time as a {@link Duration}
     */
    public Duration getCacheExpirationTime() {
        try {
            return Duration.ofSeconds(Integer.parseInt(properties.getProperty("server.cacheExpirationTime")));
        } catch (Exception e) {
            logMessage(new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Server config: Server cache expiration time is corrupt."));
            return Duration.ofSeconds(30); //Default of 30 secs in case of corrupt conf  file
        }
    }
}
