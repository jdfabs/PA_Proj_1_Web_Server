package logging;

import logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileMonitor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoggingTaskTest {
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
    public void testType() {
        LoggingTask testLoggingTask = new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "test message");

        assertTrue(testLoggingTask.getType()==LogType.Info);

    }

    @Test
    public void testMessage() {
        LoggingTask testLoggingTask = new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "test message");

        assertTrue(testLoggingTask.getMessage()=="test message");

    }

    @Test
    public void testLocation() {
        LoggingTask testLoggingTask = new LoggingTask(LogType.Info, LogLocation.ConsoleOut, "test message");

        assertTrue(testLoggingTask.getLocation()==LogLocation.ConsoleOut);

    }
}