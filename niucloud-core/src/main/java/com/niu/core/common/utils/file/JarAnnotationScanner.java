package com.niu.core.common.utils.file;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public final class JarAnnotationScanner {

    private File jarFile;

    public JarAnnotationScanner(File jarFile) {
        this.jarFile = jarFile;
    }

    /**
     * 扫描
     *
     * @param annotationSrcClass
     */
    public Class<?> scan(Class<? extends Annotation> annotationSrcClass) {
        try (JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile))) {
            ZipEntry zipEntry;
            while ((zipEntry = jarInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (entryName.endsWith(".class")) {
                    String className = entryName.replace(".class", "").replaceAll("/", ".");
                    className = className.replaceAll("\\$", ".");
                    byte[] classData = loadClassData(jarInputStream);
                    JarClassLoader jarClassLoader = new JarClassLoader(classData);
                    try {
                        Class<?> clazz = jarClassLoader.findClass(className);
                        Annotation annotationTargetClass = clazz.getAnnotation(annotationSrcClass);
                        if (annotationTargetClass != null) {
                            return clazz;
                        }
                    } catch (ClassNotFoundException e) {
                        // ignore
                    }
                }
                jarInputStream.closeEntry();
            }
        } catch (Exception | NoClassDefFoundError e) {
            // e.printStackTrace();
            // ignore
        }
        return null;
    }

    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    private byte[] loadClassData(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        }
    }

    private class JarClassLoader extends ClassLoader {

        private byte[] classData;

        public JarClassLoader(byte[] classData) {
            this.classData = classData;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    /**
     * @param clazz
     * @return
     */
    public static String getPackageName(Class<?> clazz) {
        if (clazz != null) {
            return clazz.getName().substring(0, clazz.getName().lastIndexOf('.'));
        }
        return "";
    }

    public static void main(String[] args) {
        File jarFile = new File("D:\\Temp\\20240625\\addon\\shop-1.0.0.jar");
        JarAnnotationScanner jarAnnotationScanner = new JarAnnotationScanner(jarFile);
        Class<?> clazz = jarAnnotationScanner.scan(SpringBootApplication.class);
        System.out.println(clazz.getName());
        System.out.println(getPackageName(clazz));
    }

}
