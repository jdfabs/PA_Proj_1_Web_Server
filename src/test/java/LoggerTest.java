import config.ServerConfig;
import logging.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggerTest {

    // Streams to capture System.out and System.err
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ServerConfig config;


    @BeforeEach
    public void setup() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        config = new ServerConfig("src/test/java/resources/server.config");
        SharedBuffer.buffer.clear();
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testInfoMethod() throws InterruptedException {
        Logger logger = new Logger(config);
        logger.start();

        LoggingTask task = new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Test info message");

        SharedBuffer.buffer.add(task);

        while (!SharedBuffer.buffer.isEmpty()) { //wait until LogTask is consumed
            TimeUnit.MILLISECONDS.sleep(100);
        }

        TimeUnit.MILLISECONDS.sleep(500); //Give the Logger 0.5 sec to output message

        assertTrue(outContent.toString().contains("[INFO] Test info message"));

        logger.shutdown().join(); //shutdown self returns to make this a small oneliner *kiss* *kiss*
    }

    @Test
    public void testErrorMethod() throws InterruptedException {
        Logger logger = new Logger(config);
        logger.start();

        LoggingTask task = new LoggingTask(LogType.Error, LogLocation.ConsoleErr, "Test error message");

        SharedBuffer.buffer.add(task);

        while (!SharedBuffer.buffer.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        TimeUnit.MILLISECONDS.sleep(500);

        assertTrue(errContent.toString().contains("[ERROR] Test error message"));

        logger.shutdown().join();
    }

    @Test
    public void testWarningMethod() throws InterruptedException {
        Logger logger = new Logger(config);
        logger.start();

        LoggingTask task = new LoggingTask(LogType.Warning, LogLocation.ConsoleErr, "Test warning message");

        SharedBuffer.buffer.add(task);

        while (!SharedBuffer.buffer.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        TimeUnit.MILLISECONDS.sleep(500);

        assertTrue(errContent.toString().contains("[WARNING] Test warning message"));

        logger.shutdown().join();
    }

    @Test
    public void testLoggingTaskGetters() {
        LocalDateTime before = LocalDateTime.now();
        LoggingTask task = new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Task message");
        assertEquals(LogType.Info, task.getType());
        assertEquals(LogLocation.ConsoleOut, task.getLocation());
        assertEquals("Task message", task.getMessage());



        assertTrue(task.getRequestTime().isAfter(before.minusSeconds(1)) && task.getRequestTime().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    public void testLogProducerDefaultMethod() throws InterruptedException {
        LogProducer producer = new LogProducer() {
        }; //Generic instance of the interface

        LoggingTask task = new LoggingTask(LogType.Warning, LogLocation.ConsoleErr, "Producer message");
        producer.logMessage(task);

        assertTrue(SharedBuffer.buffer.contains(task));
    }

    @Test
    public void testLoggerRunMethod() throws InterruptedException {
        Logger logger = new Logger(config);
        logger.start();

        for (int i = 0; i < 10; i++) {
            LoggingTask task = new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "Run method test " + i);
            SharedBuffer.buffer.add(task);

            TimeUnit.MILLISECONDS.sleep(100);

            assertTrue(outContent.toString().contains("[INFO] Run method test " + i));
        }

        logger.shutdown().join();
    }

    @Test
    public void testRequestMethod() throws InterruptedException {

        Logger logger = new Logger(config);
        logger.start();

        String requestMessage = "GET /api/test 200 127.0.0.1";
        LoggingTask task = new LoggingTask(LogType.Request, LogLocation.ConsoleOut, requestMessage);

        SharedBuffer.buffer.add(task);

        while (!SharedBuffer.buffer.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        TimeUnit.MILLISECONDS.sleep(500);

        String output = outContent.toString().replace("\n", "").replace("\r", "");
        String expectedJson = String.format(
                "{\"timestamp\":\"%s\",\"method\":\"GET\",\"route\":\"/api/test\",\"origin\":\"127.0.0.1\",\"status\":200}\r\n",
                task.getRequestTime().toString()
        ).replace("\n", "").replace("\r", "");
        assertTrue(output.contains(expectedJson),
                "Expected JSON request log not found in output. Expected: " + expectedJson + ", Got: " + output);

        logger.shutdown().join();
    }

    @Test
    public void testLogFileNewFile() throws IOException, InterruptedException {
        ServerConfig config = new ServerConfig("src/test/java/resources/server2.config");
        Logger logger = new Logger(config);
        logger.start();

        String requestMessage = "GET /api/test 200 127.0.0.1";
        LoggingTask task = new LoggingTask(LogType.Request, LogLocation.File, requestMessage);

        SharedBuffer.buffer.add(task);

        while (!SharedBuffer.buffer.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        TimeUnit.MILLISECONDS.sleep(500);
        String filePath = Paths.get(config.getRoot() + config.getLogPath() + "/" + config.getLogFileName() + ".log").toString();
        File newLog = new File(filePath);
        assertTrue(newLog.exists(), "Log file does not exist");
        newLog.delete();
        logger.shutdown().join();
    }

    @Test
    public void testLogFile() throws IOException, InterruptedException {
        ServerConfig config = new ServerConfig("src/test/java/resources/server.config");
        Logger logger = new Logger(config);
        logger.start();

        String requestMessage = "GET /api/test 200 127.0.0.1";
        LoggingTask task = new LoggingTask(LogType.Request, LogLocation.File, requestMessage);
        String filePath = Paths.get(config.getRoot() + config.getLogPath() + "/" + config.getLogFileName() + ".log").toString();
        File logs = new File(filePath);
        assertTrue(logs.exists(), "Log file does not exist");

        SharedBuffer.buffer.add(task);

        while (!SharedBuffer.buffer.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }
        TimeUnit.MILLISECONDS.sleep(500);

        String content = new String(Files.readAllBytes(logs.toPath()), StandardCharsets.UTF_8).replace("\n", "").replace("\r", "");

        String expectedJson = String.format(
                "{\"timestamp\":\"%s\",\"method\":\"GET\",\"route\":\"/api/test\",\"origin\":\"127.0.0.1\",\"status\":200}\r\n",
                task.getRequestTime().toString()
        ).replace("\n", "").replace("\r", "");
        assertTrue(content.contains(expectedJson),
                "Expected JSON request log not found in output. Expected: " + expectedJson + ", Got: " + content);

        logger.shutdown().join();
    }
}

