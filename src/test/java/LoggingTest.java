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
        TestLogger logger1 = new TestLogger("TestLogger1"); //mockito spy didnt help :c, had to create test subclasses
        TestLogger logger2 = new TestLogger("TestLogger2");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        logger1.start();
        logger2.start();

        for (int i = 0; i < 100; i++) {
            SharedBuffer.buffer.add(new LoggingTask(LogType.Info, LogLocation.Console, "message: " + i));
        }

        //Trying to directly check output for the new TestLogger name...
        //Previous tests all working locally but git actions are F*ng me
        boolean foundLogger1 = false, foundLogger2 = false;
        int counter = 0; //no no infinite loops
        while (!foundLogger1 || !foundLogger2 && counter < 1000) { //... intelij is drunk, saying this is allways true
            String output = outContent.toString();
            if (!foundLogger1) {
                foundLogger1 = output.contains("TestLogger1");
            }
            if (!foundLogger2) {
                foundLogger2 = output.contains("TestLogger2");
            }
            if (foundLogger1 && foundLogger2) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(10);
            counter++;
        }

        assertTrue(foundLogger1, "Expected log output for TestLogger1 not found.");
        assertTrue(foundLogger2, "Expected log output for TestLogger2 not found.");
        logger1.interrupt();
        logger2.interrupt();
    }


    @AfterAll
    static void cleanUp() {

        System.setOut(System.out); //output reset
        System.setErr(System.err);
    }


}



