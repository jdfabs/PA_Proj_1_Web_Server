package Cache;

import java.time.LocalDateTime;

/**
 * Represents a cache entry that stores file content along with its last access time.
 * <p>
 * Each cache entry contains the content as a byte array and a timestamp indicating when
 * the content was last accessed. The timestamp is updated every time the content is retrieved
 * via the {@link #getContent()} method.
 * </p>
 */
public class CacheEntry {

    /** The content of the file in bytes. */
    private final byte[] content;
    /** The timestamp representing the last time the entry was accessed. */
    private LocalDateTime lastUseTime;

    /**
     * Constructs a new {@code CacheEntry} with the specified content.
     * The last use time is initialized to the current time.
     *
     * @param content the byte array representing the content to be cached.
     */
    public CacheEntry(byte[] content) {
        this.content = content;
        this.lastUseTime = LocalDateTime.now();
    }

    /**
     * returns the cached content.
     * <p>
     * This method also updates the last use time to the current time to reflect that the
     * entry has been accessed.
     * </p>
     *
     * @return the byte array containing the cached content.
     */
    public byte[] getContent() {
        this.lastUseTime = LocalDateTime.now();
        return content;
    }

    /**
     * Returns the last time this cache entry was accessed.
     *
     * @return a {@link LocalDateTime} object representing the last access time.
     */
    public LocalDateTime getLastUseTime() {
        return lastUseTime;
    }
}