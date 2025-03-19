import logging.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class LoggingTest {
    private Logger logger1 = new Logger();
    private Logger logger2 = new Logger();

    @BeforeEach
    void setup() {
        SharedBuffer.buffer.clear();
        logger1.start();
        logger2.start();
    }

    static Stream<Arguments> provideLogMessages() {
        return Stream.of(
                Arguments.of(LogType.Info, "[INFO] TEST"),
                Arguments.of(LogType.Warning, "[WARNING] TEST"),
                Arguments.of(LogType.Error, "[ERROR] TEST")
        );
    }

    @ParameterizedTest
    @MethodSource("provideLogMessages")
    void testConsoleLogging_LogIntoCorrectLocation(LogType logType, String expectedOutput) throws InterruptedException {
        // Capture console output -- if info normal console, else (warn+error) in err output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        if (LogType.Info == logType) {
            System.setOut(new PrintStream(outContent));
        } else System.setErr(new PrintStream(outContent));

        SharedBuffer.buffer.add(new LoggingTask(logType, LogLocation.Console, "TEST"));
        TimeUnit.MILLISECONDS.sleep(100);

        // Assert that the console output contains the expected log message
        assertTrue(outContent.toString().contains(expectedOutput), "Expected log output not found.");
    }

    @Test
    void testMultipleLogs_BothWorkersShouldWork() throws InterruptedException {
        TestLogger logger1 = new TestLogger(); //mockito spy didnt help :c, had to create test subclasses
        TestLogger logger2 = new TestLogger();

        logger1.start();
        logger2.start();

        for (int i = 0; i < 100; i++) {
            SharedBuffer.buffer.add(new LoggingTask(LogType.Info, LogLocation.Console, "message: " + i));
        }


        while (!SharedBuffer.buffer.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        // Check that both TestLoggers had their info(String) method called at least once.
        assertTrue(logger1.getInfoCallCount() > 0, "logger1.info() was not called.");
        assertTrue(logger2.getInfoCallCount() > 0, "logger2.info() was not called.");

        //Kill Loggers
        logger1.interrupt();
        logger2.interrupt();
    }


    @AfterAll
    static void cleanUp() {
        System.setOut(System.out); //output reset
        System.setErr(System.err);
    }


}



