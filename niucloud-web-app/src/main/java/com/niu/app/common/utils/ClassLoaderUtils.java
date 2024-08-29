package com.niu.app.common.utils;

import java.util.ServiceLoader;

public class ClassLoaderUtils {

    /**
     * 递归打印ClassLoader的层级
     */
    public static void printClassloader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        printClassloader(classLoader);
    }

    /**
     * @param classLoader
     */
    public static void printClassloader(ClassLoader classLoader) {
        long currentTime = System.currentTimeMillis();
        printClassloader(classLoader, 10, 1, currentTime);
    }

    /**
     * @param classLoader
     * @param max
     * @param level
     */
    public static void printClassloader(ClassLoader classLoader, int max, int level, long currentTime) {
        if (level >= max) {
            return;
        }
        if (classLoader != null) {
            System.out.println("[" + level + "],[" + currentTime + "],ClassLoader: " + classLoader);
            printClassloader(classLoader.getParent(), max, level + 1, currentTime);
        }
    }

}
