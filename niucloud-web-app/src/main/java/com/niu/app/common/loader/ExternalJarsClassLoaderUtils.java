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
            CustomURLClassLoader customClassLoader = new CustomURLClassLoader(new URL[]{}, Thread.currentThread().getContextClassLoader());
            customClassLoader.addURL(jarUrl);

            // 如果需要将自定义类加载器设置为当前线程的上下文类加载器
            Thread.currentThread().setContextClassLoader(customClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CustomURLClassLoader extends URLClassLoader {

        public CustomURLClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected synchronized void addURL(URL url) {
            super.addURL(url);
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
