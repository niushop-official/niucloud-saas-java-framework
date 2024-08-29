package com.niu.core.service.core.app.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件操作助手
 */
@Slf4j
public final class AddonOperateHelper {

    public static class Cache {

        public static final Map<String, Object> cacheMap = new ConcurrentHashMap<>();

        /**
         * @param key
         * @param value
         */
        public static synchronized void put(String key, Object value) {
            cacheMap.put(key, value);
            log.info("cache map -> " + cacheMap);
        }

        /**
         * @param key
         * @param <T>
         * @return
         */
        public static synchronized <T> T get(String key) {
            log.info("cache map -> " + cacheMap);
            return (T) cacheMap.get(key);
        }

        /**
         * @param key
         */
        public static synchronized void remote(String key) {
            log.info("cache map -> " + cacheMap);
            cacheMap.remove(key);
        }

        /**
         * 清除
         */
        public static synchronized void clear() {
            cacheMap.clear();
        }

        public static Map<String, Object> getCache() {
            return cacheMap;
        }

        public static Set<String> getCacheKey() {
            return cacheMap.keySet();
        }

    }

    public static class Tips {

        public static final List<String> tipsList = new ArrayList<String>();

        public static synchronized void add(String element) {
            tipsList.add(element);
        }

        public static synchronized List<String> getTipsList() {
            return tipsList;
        }

    }

}
