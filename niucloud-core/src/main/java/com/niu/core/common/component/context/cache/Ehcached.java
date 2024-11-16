package com.niu.core.common.component.context.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.*;

/**
 * ehcache实现
 */
public final class Ehcached implements Cached {

    private CacheManager cacheManager;
    private Cache cache;

    /**
     * 注： 这里的CacheManager为： net.sf.ehcache.CacheManager
     *
     * @param cacheManager
     */
    public Ehcached(CacheManager cacheManager) {
        this(cacheManager, "systemCache");
    }

    public Ehcached(CacheManager cacheManager, String name) {
        this.cacheManager = cacheManager;
        this.cache = cacheManager.getCache(name);
    }

    @Override
    public void put(String key, Object value) {
        Element element = new Element(key, value);
        this.cache.put(element);
    }

    @Override
    public void put(String key, Object value, long timeoutSeconds) {
        Element element = new Element(key, value);
        element.setTimeToIdle((int) timeoutSeconds);
        this.cache.put(element);
    }

    @Override
    public Object get(String key) {
        Element element = this.cache.get(key);
        if (element == null) {
            return null;
        }
        return element.getObjectValue();
    }

    @Override
    public void remove(String... key) {
        for (String k : key) {
            cache.remove(k);
        }
    }
    
    @Override
    public Collection<String> getAllKeys() {
        return cache.getKeys();
    }

    @Override
    public Collection<Object> getAllValues() {
        Set<Object> objectSet = new HashSet<>();
        for (Object key : cache.getKeys()) {
            Element element = this.cache.get(key);
            if (element != null) {
                objectSet.add(element.getObjectValue());
            }
        }
        return objectSet;
    }

    @Override
    public Map<String, Object> getAllKeyValues() {
        Map<String, Object> objectMap = new HashMap<>();
        for (Object key : cache.getKeys()) {
            Element element = this.cache.get(key);
            if (element != null) {
                objectMap.put(key + "", element.getObjectValue());
            }
        }
        return objectMap;
    }

    @Override
    public void removeBatch(String pattern) {
        Map<String, Object> objectMap = new HashMap<>();
        for (Object key : cache.getKeys()) {
            Element element = this.cache.get(key);
            if (element != null && key instanceof String) {
                String stringKey = (String) key;
                if (stringKey.startsWith(pattern)) {
                    objectMap.remove(stringKey);
                }
            }
        }
    }

}
