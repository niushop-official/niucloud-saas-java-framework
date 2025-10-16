package com.niu.core.common.component.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程本地变量工具类，用于存储和获取线程上下文信息
 */
public final class ThreadLocalHolder {

    // 私有构造函数，防止实例化
    private ThreadLocalHolder() {
        throw new AssertionError("工具类不允许实例化");
    }

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    /**
     * 存储键值对到线程本地变量
     * @param key 键
     * @param value 值
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
     * 获取线程本地变量中的值
     * @param key 键
     * @return 对应的值，不存在则返回null
     */
    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    /**
     * 获取指定类型的线程本地变量值
     * @param key 键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象，null或转换失败时返回null
     */
    public static <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        try {
            return clazz.cast(value);
        } catch (ClassCastException e) {
            // 类型转换失败时返回null，可根据需要添加日志
            return null;
        }
    }

    /**
     * 移除线程本地变量中的所有值
     */
    public static void remove() {
        threadLocal.remove();
    }

    /**
     * 移除线程本地变量中指定键的值
     * @param key 要移除的键
     */
    public static void remove(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            map.remove(key);
            // 如果map为空，可选择清除ThreadLocal以释放资源
            if (map.isEmpty()) {
                threadLocal.remove();
            }
        }
    }

    /**
     * 获取Integer类型的值
     * @param key 键
     * @return 转换后的Integer，失败则返回0
     */
    public static Integer getInteger(String key){
        Object result = get(key);
        try{
            return (Integer) result;
        }catch (Exception e){
            // 类型转换失败返回默认值
            return 0;
        }
    }

    /**
     * 获取Long类型的值
     * @param key 键
     * @return 转换后的Long，失败则返回0L
     */
    public static Long getLong(String key){
        Object result = get(key);
        try{
            return (Long) result;
        }catch (Exception e){
            // 类型转换失败返回默认值
            return 0L;
        }
    }

    /**
     * 存储Integer类型值
     * @param key 键
     * @param value 值
     */
    public static void putInteger(String key, Integer value) {
        put(key, value);
    }

    /**
     * 获取String类型的值
     * @param key 键
     * @return 转换后的String，失败则返回空字符串
     */
    public static String getString(String key){
        Object result = get(key);
        try{
            return (String) result;
        }catch (Exception e){
            // 类型转换失败返回默认值
            return "";
        }
    }

    /**
     * 存储String类型值
     * @param key 键
     * @param value 值
     */
    public static void putString(String key, String value) {
        put(key, value);
    }
}
