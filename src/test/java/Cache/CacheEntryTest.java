package Cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CacheEntryTest {
    private CacheEntry testCacheEntry;

    @BeforeEach
    public void setUp(){
        testCacheEntry = mock(CacheEntry.class);
    }

    @Test
    public void CacheEntryTest(){

        CacheEntry testCacheEntry = new CacheEntry("Hello World".getBytes());

        byte[] testStringBytes = "Hello World".getBytes();
        LocalDateTime LocalDateTimeTest = LocalDateTime.now();

        assertEquals(testStringBytes, testCacheEntry.getContent());
        assertEquals(LocalDateTimeTest, testCacheEntry.getLastUseTime());


    }

    @Test
    public void getContentTest(){
        assertEquals("Hello World".getBytes(), testCacheEntry.getContent());
    }

    @Test
    public void getLastUseTimeTest(){
        assertEquals(LocalDateTime.now(), testCacheEntry.getLastUseTime());
    }

}