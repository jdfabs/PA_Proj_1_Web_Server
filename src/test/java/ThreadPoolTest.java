import core.ThreadPool;
import core.WorkerThread;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadPoolTest {

    @Test
    public void testSingleTaskExecution() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1);
        var ref = new Object() {
            int counter = 0;
        };
        Runnable task = () -> ref.counter++;
        pool.execute(task);
        Thread.sleep(100); // waits for the task to be executed
        assertEquals(1, ref.counter);
        pool.shutdown();
    }

    @Test
    public void testMultipleTaskExecution() throws InterruptedException {
        ThreadPool pool = new ThreadPool(2);
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = counter::incrementAndGet;
        pool.execute(task);
        pool.execute(task);
        pool.execute(task);
        Thread.sleep(200); // waits for the tasks to be executed
        assertEquals(3, counter.get()); //More tasks than workers
        pool.shutdown();
    }

    @Test
    public void testTaskQueueing() throws InterruptedException {
        ThreadPool pool = new ThreadPool(2);
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = () -> {
            try {
                Thread.sleep(100); // Simulates a long task
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        for (int i = 0; i < 5; i++) {
            pool.execute(task);
        }
        Thread.sleep(1000); // Waits for all tasks to be executed
        assertEquals(5, counter.get());
        pool.shutdown();
    }

    @Test
    public void testExecuteAfterShutdown() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1);
        pool.shutdown();
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = counter::incrementAndGet;
        pool.execute(task);
        Thread.sleep(100);
        assertEquals(0, counter.get());
    }

    @Test
    public void testWorkerInterruption() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1);
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = () -> {
            try {
                Thread.sleep(500);
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        pool.execute(task);
        Thread.sleep(100);
        pool.shutdown();
        Thread.sleep(100);
        assertEquals(0, counter.get());
    }

    @Test
    public void testTaskWithException() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1);
        AtomicInteger counter = new AtomicInteger(0);
        Runnable failingTask = () -> {
            throw new RuntimeException("Test exception");
        };
        Runnable successfulTask = counter::incrementAndGet;
        pool.execute(failingTask);
        pool.execute(successfulTask);
        Thread.sleep(200);
        assertEquals(1, counter.get());
        pool.shutdown();
    }

    @Test
    public void testWorkerReuse() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1);
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = counter::incrementAndGet;
        for (int i = 0; i < 3; i++) {
            pool.execute(task);
        }
        Thread.sleep(200);
        assertEquals(3, counter.get());
        pool.shutdown();
    }
}