package Cache;

/**
 * Singleton class that provides a single shared instance of {@link CacheManager}, using the Singleton Pattern.
 * <p>
 * This ensures that the caching system is consistent and centralized.
 * All components accessing the cache should do so via {@code CacheManagerSingleton.getInstance()}.
 */
public class CacheManagerSingleton {
    /** The single instance of the cache manager used throughout the application. */
    private static final CacheManager instance = new CacheManager();

    /**
     * Private constructor to prevent instantiation.
     * This class is intended to be used via {@link #getInstance()} only.
     */
    private CacheManagerSingleton() {
    }

    /**
     * Returns the shared instance of {@link CacheManager}.
     *
     * @return the singleton {@code CacheManager} instance
     */
    public static CacheManager getInstance() {
        return instance;
    }
}