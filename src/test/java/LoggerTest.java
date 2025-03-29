import config.ServerConfig;
import logging.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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



        assertTrue(task.getRequestTime().isAfter(before) && task.getRequestTime().isBefore(LocalDateTime.now()));
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
}

