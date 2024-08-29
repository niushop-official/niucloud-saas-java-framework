package com.niu.core.common.utils.cache;

public class CacheTag {

    public static String defaultKey = "default";

    /**
     * @param clazz
     * @return
     */
    public static String key(Class clazz) {
        if (clazz == null) {
            return "defaultKey";
        }
        return clazz.getSimpleName();
    }

}
