package com.niu.core.common.config.dataization;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Slf4j
public final class AddonModuleContext {

    public static boolean isInited = false;

    // 同步的HashMap
    private static Map<String, Class<?>> addonModuleMap = Collections.synchronizedMap(new HashMap<>());

    // 系统资源加载器
    private static ResourceLoader systemResourceLoader;

    /**
     * @param systemResourceLoader
     */
    public static void setSystemResourceLoader(ResourceLoader systemResourceLoader) {
        AddonModuleContext.systemResourceLoader = systemResourceLoader;
    }

    /**
     * @return
     */
    public static ResourceLoader getSystemResourceLoader() {
        return AddonModuleContext.systemResourceLoader;
    }

    /**
     * 是否初始化
     *
     * @return
     */
    public static boolean isInited() {
        return isInited;
    }

    /**
     * @param addonModuleCode
     * @param relativePath
     * @return
     */
    public static String readResourceAsStreamToText(String addonModuleCode, String relativePath) {
        if (StrUtil.isNotEmpty(relativePath) && relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1, relativePath.length());
        }
        String context = "";
        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            boolean readResult = false;
            if (!readResult) {
                // 如果在开发环境下，当前类加载器相对的位置读取
                InputStream inputStream = null;
                try {
                    String devClassPath = addonModuleCode + "/" + relativePath;
                    inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(devClassPath);
                    if (inputStream == null) {
                        log.info("开发环境下无法读取文件：" + devClassPath);
                    } else {
                        context = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                        readResult = true;
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                    }
                }
            }
            if (!readResult) {
                // 如果在开发环境下，当前类所在的位置下读取
                Class<?> largeModuleClazz = addonModuleMap.get(addonModuleCode);
                if (largeModuleClazz != null) {
                    InputStream inputStream = null;
                    try {
                        String devClassPath = addonModuleCode + "/" + relativePath;
                        inputStream = largeModuleClazz.getResourceAsStream(devClassPath);
                        if (inputStream == null) {
                            log.info("开发环境下无法读取文件：" + devClassPath);
                        } else {
                            context = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
//                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (!readResult) {
                InputStream inputStream = null;
                try {
                    Class<?> largeModuleClazz = addonModuleMap.get(addonModuleCode);
                    String codeBasePath = largeModuleClazz.getProtectionDomain().getCodeSource().getLocation().getPath();
                    System.out.println("codeBasePath = " + codeBasePath);
                    String devClassPath = addonModuleCode + "/loader/" + relativePath;
                    inputStream = new FileInputStream(codeBasePath + devClassPath);
                    if (inputStream == null) {
                        log.info("开发环境下无法读取文件：" + devClassPath);
                    } else {
                        context = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            boolean readResult = false;
            if (!readResult) {
                // 如果在生产模式下，classpath下去读
                InputStream inputStream = null;
                try {
                    String runtimeClassPath = "classpath:" + addonModuleCode + "/" + relativePath;
                    inputStream = getSystemResourceLoader().getResource(runtimeClassPath).getInputStream();
                    if (inputStream == null) {
                        log.error("当前环境<ResourceLoader>无法读取文件：" + runtimeClassPath);
                    } else {
                        context = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                        readResult = true;
                    }
                } catch (Exception e) {
                    // ignore
//                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                    }
                }
            }
            if (!readResult) {
                // 如果在生产模式下，jar包内部读取
                Class<?> addonModuleClazz = addonModuleMap.get(addonModuleCode);
                InputStream openInputStream = null;
                JarInputStream inputStream = null;
                try {
                    String jarPath = (addonModuleClazz.getProtectionDomain().getCodeSource().getLocation().getPath());
                    String jarProtocol = "jar:" + jarPath;
                    URL url = URI.create(jarProtocol).toURL();
                    openInputStream = url.openStream();
                    JarInputStream jarInputStream = new JarInputStream(openInputStream);
                    String innerEntry = addonModuleCode + "/" + relativePath;
                    JarEntry entry;
                    while ((entry = jarInputStream.getNextJarEntry()) != null) {
                        if (entry.getName().equals(innerEntry)) {
                            inputStream = jarInputStream;
                        } else {
                            jarInputStream.closeEntry();
                        }
                    }
                    if (inputStream == null) {
                        log.error("当前环境<URL>无法读取文件：" + jarProtocol + "!" + innerEntry);
                    } else {
                        context = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                    }
                    if (openInputStream != null) {
                        try {
                            openInputStream.close();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return context;
    }

    /**
     * @param largeModuleCode
     * @param relativePath
     * @return
     */
    public static Properties readResourceAsStreamToProperties(String largeModuleCode, String relativePath) {
        if (StrUtil.isNotEmpty(relativePath) && relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1, relativePath.length());
        }
        Properties properties = new Properties();
        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            boolean readResult = false;
            if (!readResult) {
                // 如果在开发环境下，当前类加载器相对的位置读取
                InputStream inputStream = null;
                try {
                    String devClassPath = largeModuleCode + "/" + relativePath;
                    inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(devClassPath);
                    if (inputStream == null) {
                        log.error("开发环境下无法读取文件：" + devClassPath);
                    } else {
                        properties.load(inputStream);
                        readResult = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (!readResult) {
                // 如果在开发环境下，当前类所在的位置下读取
                Class<?> largeModuleClazz = addonModuleMap.get(largeModuleCode);
                if (largeModuleClazz != null) {
                    InputStream inputStream = null;
                    try {
                        String devClassPath = "/" + largeModuleCode + "/" + relativePath;
                        inputStream = largeModuleClazz.getResourceAsStream(devClassPath);
                        if (inputStream == null) {
                            log.error("开发环境下无法读取文件：" + devClassPath);
                        } else {
                            properties.load(inputStream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else {
            boolean readResult = false;
            if (!readResult) {
                // 如果在生产模式下，classpath下去读
                InputStream inputStream = null;
                try {
                    String runtimeClassPath = "classpath:" + largeModuleCode + "/" + relativePath;
                    inputStream = getSystemResourceLoader().getResource(runtimeClassPath).getInputStream();
                    if (inputStream == null) {
                        log.error("当前环境<ResourceLoader>无法读取文件：" + runtimeClassPath);
                    } else {
                        properties.load(inputStream);
                        readResult = true;
                    }
                } catch (Exception e) {
                    // ignore
                    // e.printStackTrace();
                    log.warn("当前环境<ResourceLoader>无法读取文件：" + e.getMessage());
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (!readResult) {
                // 如果在生产模式下，jar包内部读取
                Class<?> largeModuleClazz = addonModuleMap.get(largeModuleCode);
                InputStream openInputStream = null;
                JarInputStream inputStream = null;
                try {
                    String jarPath = (largeModuleClazz.getProtectionDomain().getCodeSource().getLocation().getPath());
                    String jarProtocol = "jar:" + jarPath;
                    URL url = URI.create(jarProtocol).toURL();
                    openInputStream = url.openStream();
                    JarInputStream jarInputStream = new JarInputStream(openInputStream);
                    String innerEntry = largeModuleCode + "/" + relativePath;
                    JarEntry entry;
                    while ((entry = jarInputStream.getNextJarEntry()) != null) {
                        if (entry.getName().equals(innerEntry)) {
                            inputStream = jarInputStream;
                        } else {
                            jarInputStream.closeEntry();
                        }
                    }
                    if (inputStream == null) {
                        log.error("当前环境<URL>无法读取文件：" + jarProtocol + "!" + innerEntry);
                    } else {
                        properties.load(inputStream);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (openInputStream != null) {
                        try {
                            openInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return properties;
    }


    /**
     * @param addonModuleCode
     * @param relativePath
     * @return
     */
    public static InputStream getResourceAsStream(String addonModuleCode, String relativePath) {
        InputStream inputStream = null;
        boolean readResult = false;
        if (!readResult) {
            Class<?> largeModuleClazz = addonModuleMap.get(addonModuleCode);
            if (largeModuleClazz != null) {
                try {
                    String devClassPath = "/" + addonModuleCode + "/" + relativePath;
                    inputStream = largeModuleClazz.getResourceAsStream(devClassPath);
                    if (inputStream == null) {
                        log.error("当前环境无法读取文件：" + devClassPath);
                    } else {
                        readResult = true;
                    }
                } catch (Exception e) {
                    // ignore
                    e.printStackTrace();
                }
            }
        }
        if (!readResult) {
            try {
                String runtimeClassPath = "classpath:" + addonModuleCode + "/" + relativePath;
                inputStream = getSystemResourceLoader().getResource(runtimeClassPath).getInputStream();
                if (inputStream == null) {
                    log.error("当前环境<ResourceLoader>无法读取文件：" + runtimeClassPath);
                }
            } catch (Exception e) {
                // ignore
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 初始化
     */
    public static void init(Map<String, String> envsMap) {
        if (isInited) {
            return;
        }
        if (envsMap == null) {
            envsMap = new HashMap<>();
            envsMap.put("core", "com.niu.core.WebAppApplication");
        } else {
            envsMap.put("core", "com.niu.core.WebAppApplication");
        }
        log.warn("envsMap >>>>>>>>>>>>>>>>> " + envsMap);
        Set<String> keySet = envsMap.keySet();
        for (String largeModule : keySet) {
            try {
                add(largeModule, envsMap.get(largeModule));
            } catch (Exception e) {
                // e.printStackTrace();
                log.error("装载模块：" + largeModule + "失败.");
            }
        }
        log.warn("addonModuleMap >>>>>>>>>>>>>>>>> " + addonModuleMap);
        isInited = true;
    }

    /**
     * 动态加载类摸板
     *
     * @param className
     * @return
     */
    private static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            // ignore
        }
        return null;
    }

    /**
     * @param addonModule
     */
    public static void add(String addonModule, String classTemplate) {
        Class<?> classTemplateClass = classForName(classTemplate);
        if (classTemplateClass != null) {
            addonModuleMap.put(addonModule, classTemplateClass);
        }
    }

    /**
     * @param addonModule
     */
    public static void remove(String addonModule) {
        addonModuleMap.remove(addonModule);
    }

    /**
     * 获取所有的子模块
     *
     * @return
     */
    public static Set<String> getAddonModules() {
        return addonModuleMap.keySet();
    }

    /**
     *
     * @return
     */
    public static Map<String, Class<?>> getAddonModuleMap() { return addonModuleMap; }
}
