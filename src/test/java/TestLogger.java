import logging.Logger;

import java.util.concurrent.TimeUnit;

public class TestLogger extends Logger {
    //Mockito spy(Logger) did not allow method calls inside the spyed class, created this class to test multiple calls
    private int infoCallCount = 0;

    public TestLogger() {
        super();
    }

    @Override
    public void info(String message) {
        infoCallCount++;
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        super.info(message);
    }

    public int getInfoCallCount() {
        return infoCallCount;
    }
}
