package logging;

import com.sun.jdi.InvalidTypeException;

/**
 * A logger that prints log messages.
 * This class will later be expanded to log to the log file!!
 */
public class Logger extends Thread implements SharedBuffer {

    public void run(){
        while (true){
            try{
                LoggingTask loggingTask = buffer.poll();
                switch (loggingTask.getType()) {
                    case Info:
                        info(loggingTask.getMessage());
                        break;
                    case Error:
                        error(loggingTask.getMessage());
                        break;
                    case Warning:
                        warning(loggingTask.getMessage());
                        break;
                    default:
                        throw new InvalidTypeException();
                }
            } catch (Exception e){

            }
        }
    }

//TODO write in specific file using parameters

    /**
     * Prints Information
     *
     * @param message The message which will be on the log
     */
    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    /**
     * Prints Errors
     *
     * @param message The message which will be on the log
     */
    public void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    /**
     * Prints Warnings
     *
     * @param message The message which will be on the log
     */
    public void warning(String message) {
        System.err.println("[WARNING] " + message);
    }
}