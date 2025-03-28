package logging;

import logging.Logger;
import logging.LoggingTask;
import logging.SharedBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileMonitor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoggerTest {


    private Logger testLogger;
    private SharedBuffer testBuffer;
    private LoggingTask testLoggingTask;

    @BeforeEach
    public void setUp() {
        testLogger = mock(Logger.class);
        testBuffer = mock(SharedBuffer.class);
        testLoggingTask = mock(LoggingTask.class);
    }

    @Test

    /**
     * assures that test message is printed out corretly
     */
    public void testInfoLogger(){
        LoggingTask testLoggingTask = new LoggingTask(LogType.Info, LogLocation.File, "test message");

        assertEquals(testLoggingTask.getType(), LogType.Info);
        assertEquals(testLoggingTask.getLocation(), LogLocation.File);
        assertEquals(testLoggingTask.getMessage(), "test message");

    }

    public void testErrorLogger(){
        LoggingTask testLoggingTask = new LoggingTask(LogType.Error, LogLocation.File, "test message");

        assertEquals(testLoggingTask.getType(), LogType.Error);
        assertEquals(testLoggingTask.getLocation(), LogLocation.File);
        assertEquals(testLoggingTask.getMessage(), "test message");

    }

    public void testWarningLogger(){
        LoggingTask testLoggingTask = new LoggingTask(LogType.Warning, LogLocation.File, "test message");

        assertEquals(testLoggingTask.getType(), LogType.Warning);
        assertEquals(testLoggingTask.getLocation(), LogLocation.File);
        assertEquals(testLoggingTask.getMessage(), "test message");

    }

    public void testFileLogger(){
        LoggingTask testLoggingTask = new LoggingTask(LogType.Error, LogLocation.File, "test message");

        assertEquals(testLoggingTask.getType(), LogType.Error);
        assertEquals(testLoggingTask.getLocation(), LogLocation.File);
        assertEquals(testLoggingTask.getMessage(), "test message");

    }

}