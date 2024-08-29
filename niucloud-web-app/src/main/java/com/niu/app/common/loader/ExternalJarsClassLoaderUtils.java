package com.niu.app.common.loader;

import cn.hutool.core.lang.JarClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

public class ExternalJarsClassLoaderUtils {

    /**
     * @param values
     */
    public static void loadJars(Collection<String> values) {
        if (values != null && values.size() > 0) {
            values.stream().forEach(v -> addJarToCurrentClassLoader(v));
        }
    }

    /**
     * 添加jar到当前类加载器
     *
     * @param jarPath
     * @return
     */
    public static void addJarToCurrentClassLoader(String jarPath) {
        try {
            File jarFile = new File(jarPath);
            URL jarUrl = jarFile.toURI().toURL();
            URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, jarUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param jarPath
     * @return
     */
    public static void addJarToCurrentClassLoaderByCommon(String jarPath) {
        try {
            File jarFile = new File(jarPath);
            URL jarUrl = jarFile.toURI().toURL();
            // URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            JarClassLoader classLoader = (JarClassLoader) Thread.currentThread().getContextClassLoader();
            classLoader.addJar(jarFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
