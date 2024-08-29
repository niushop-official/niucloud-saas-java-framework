package com.niu.core.component.context.cache;

import net.sf.ehcache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

public final class CacheFactory {

    private static Cached ehcacheOperator;
    private static Cached redisCached;

    /**
     * @param cacheManager
     * @return
     */
    public static Cached buildWithEhcache(CacheManager cacheManager) {
        ehcacheOperator = new Ehcached(cacheManager);
        return ehcacheOperator;
    }

    /**
     * @param redisTemplate
     * @return
     */
    public static Cached buildWithRedis(RedisTemplate<String, Object> redisTemplate) {
        redisCached = new RedisCache(redisTemplate);
        return redisCached;
    }

    /**
     * @return
     */
    public static Cached getEhcacheOperator() {
        return ehcacheOperator;
    }

    /**
     * @return
     */
    public static Cached getRedisCacheOperator() {
        return redisCached;
    }

    /**
     * @return
     */
    public static Cached getCacheOperator() {
        return getRedisCacheOperator();
    }

}
