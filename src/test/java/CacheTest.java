import Cache.CacheEntry;
import Cache.CacheManager;
import Cache.CacheManagerSingleton;
import org.junit.jupiter.api.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = CacheManagerSingleton.getInstance();
        cacheManager.setExpirationTime(Duration.ofSeconds(5));
        if (!cacheManager.isAlive()) {
            cacheManager.start();
        }
    }

    @Test
    void testSingletonInstance() {
        CacheManager instance1 = CacheManagerSingleton.getInstance();
        CacheManager instance2 = CacheManagerSingleton.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testWriteAndReadFromCache() {
        String path = "/test/file.txt";
        byte[] content = "Hello Cache".getBytes();

        cacheManager.writeToCache(path, content);
        byte[] cachedContent = cacheManager.readFromCache(path);

        assertNotNull(cachedContent);
        assertArrayEquals(content, cachedContent);
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        String path = "/test/expire.txt";
        byte[] content = "Expiring Content".getBytes();

        cacheManager.writeToCache(path, content);
        assertNotNull(cacheManager.readFromCache(path));

        TimeUnit.SECONDS.sleep(10); //Cache removes entries once every 10 seconds

        assertNull(cacheManager.readFromCache(path));
    }

    @Test
    void testConcurrentCacheAccess() throws InterruptedException {
        String path = "/test/concurrent.txt";
        byte[] content = "Concurrent Content".getBytes();

        cacheManager.writeToCache(path, content);

        Runnable readTask = () -> {
            byte[] result = cacheManager.readFromCache(path);
            assertNotNull(result);
            assertArrayEquals(content, result);
        };

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(readTask);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));
    }

    @Test
    void testCacheEntryUpdatesLastUseTime() throws InterruptedException {
        CacheEntry entry = new CacheEntry("content".getBytes());
        LocalDateTime originalTime = entry.getLastUseTime();

        TimeUnit.MILLISECONDS.sleep(100);
        entry.getContent();

        assertTrue(entry.getLastUseTime().isAfter(originalTime));
    }

    @AfterAll
    static void cleanUp() {
        CacheManagerSingleton.getInstance().interrupt();
    }
}
