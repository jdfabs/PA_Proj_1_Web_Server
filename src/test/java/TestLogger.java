import logging.Logger;

import java.util.concurrent.TimeUnit;

public class TestLogger extends Logger {
    //Mockito spy(Logger) did not allow method calls inside the spyed class, created this class to test multiple calls
    private String name;

    public TestLogger(String name) {
        super();
        this.name = name;
    }

    @Override
    public void info(String message) {
        System.out.println(this.name + ": " + message);
        super.info(message);
    }
}
