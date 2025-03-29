import core.ThreadPool;
import core.WorkerThread;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest
    @ValueSource(ints = {3,5})
    public void testTaskQueueing(int count) throws InterruptedException {
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
        for (int i = 0; i < count; i++) {
            pool.execute(task);
        }
        Thread.sleep(200L * count); // Waits for all tasks to be executed
        assertEquals(count, counter.get());
        pool.shutdown();
    }

    @Test
    public void testExecuteAfterShutdown() throws InterruptedException {
        ThreadPool pool = new ThreadPool(1);
        pool.shutdown();
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = counter::incrementAndGet;
        pool.execute(task);
        Thread.sleep(500);
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
    public void testConstructorCreatesCorrectNumberOfWorkers() throws InterruptedException {
        int poolSize = 3;
        ThreadPool pool = new ThreadPool(poolSize);
        WorkerThread[] workers = pool.getWorkers();
        assertEquals(poolSize, workers.length);
        for (WorkerThread worker : workers) {
            assertTrue(worker.isAlive());
        }
        pool.shutdown();
    }

    @Test
    public void testShutdownInterruptsWorkers() throws InterruptedException {
        ThreadPool pool = new ThreadPool(2);
        WorkerThread[] workers = pool.getWorkers();

        AtomicInteger counter = new AtomicInteger(0);
        Runnable longTask = () -> {
            try {
                Thread.sleep(1000);
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        pool.execute(longTask);
        pool.execute(longTask);

        pool.shutdown();
        Thread.sleep(100);

        for (WorkerThread worker : workers) {
            assertTrue(worker.isInterrupted() || !worker.isAlive(),
                    "Worker should be interrupted or terminated after shutdown");
        }
    }
}