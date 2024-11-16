package com.niu.core.common.component.base;

import java.util.HashMap;
import java.util.Map;

public final class ThreadLocalHolder {

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal();

    /**
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);
        threadLocal.set(map);
    }

    /**
     * @param key
     * @return
     */
    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }


    /**
     * 移除所有
     */
    public static void remove() {
        threadLocal.remove();
    }

    /**
     * @param key
     * @return
     */
    public static Integer getInteger(String key){
        Object result = get(key);
        try{
            return (Integer)result;
        }catch (Exception e){
            // 防止类型错误
        }
        return 0;
    }

    /**
     * @param key
     * @return
     */
    public static Long getLong(String key){
        Object result = get(key);
        try{
            return (Long)result;
        }catch (Exception e){
            // 防止类型错误
        }
        return 0L;
    }

    public static void putInteger(String key, Integer value) {
        put(key,value);
    }


    /**
     * @param key
     * @return
     */
    public static String getString(String key){
        Object result = get(key);
        try{
            return (String)result;
        }catch (Exception e){
            // 防止类型错误
        }
        return "";
    }


    public static void putString(String key, String value) {
        put(key,value);
    }


}
