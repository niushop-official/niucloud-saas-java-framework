package com.niu.app.common.loader;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.niu.app.common.loader.parse.AbstractParser;
import com.niu.app.common.loader.parse.FileAbstractParser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.*;

/**
 * 装载外部jar，与SpringBoot一起启动
 */
@Slf4j
public class ExternalJarsLoaderPlugin {

    /**
     * 获取运行时路径
     *
     * @return
     */
    public static String getRuntimePath() {
        String mainExecutePath = ExternalJarsLoaderPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (StrUtil.isNotEmpty(mainExecutePath)) {
            if (mainExecutePath.contains("/!BOOT-INF/")) {
                mainExecutePath = mainExecutePath.substring(0, mainExecutePath.indexOf("/!BOOT-INF/"));
                if (mainExecutePath.contains("/")) {
                    mainExecutePath = mainExecutePath.substring(0, mainExecutePath.lastIndexOf('/'));
                    try {
                        mainExecutePath = new URI(mainExecutePath).toURL().getPath();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
        if (mainExecutePath.startsWith("nested:/")) {
            mainExecutePath = mainExecutePath.substring(7);
        }
        return mainExecutePath;
    }

    /**
     * @param argList
     */
    public static void load(List<String> argList, boolean guard) {
        if (!guard) {
            return;
        }
        String runtimePath = getRuntimePath();
        System.out.println("运行时路径：" + runtimePath);
        loadLibs(argList, runtimePath);
        loadAddons(argList, runtimePath);
    }

    /**
     * 加载类库
     *
     * @param argList
     * @param runtimePath
     */
    private static void loadLibs(List<String> argList, String runtimePath) {
        File libFile = new File(runtimePath + "/lib/");
        if (libFile.exists()) {
            File[] subFiles = libFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".jar");
                }
            });
            if (subFiles == null || subFiles.length <= 0) {
                return;
            }
            Arrays.stream(subFiles).forEach(jarFile -> {
                ExternalJarsClassLoaderUtils.addJarToCurrentClassLoader(jarFile.getAbsolutePath());
            });
        }
    }

    /**
     * 加载插件
     *
     * @param argList
     * @param runtimePath
     * @return
     */
    private static String[] loadAddons(List<String> argList, String runtimePath) {
        String[] params = null;
        try {
            AbstractParser parser = new FileAbstractParser(runtimePath);
            ExternalJarsClassLoaderUtils.loadJars(parser.toJarMap().values());
            Map<String, String> addonMap = parser.toAddonMap();
            String addonJson = JSON.toJSONString(addonMap);
            argList.add("addon=" + addonJson);
            System.out.println("插件装载成功.");
        } catch (Exception e) {
            System.err.println("插件装载失败: " + e.getMessage());
        }
        return params;
    }

    /**
     * @param args
     * @param argList
     * @return
     */
    public static String[] merge(String[] args, final List<String> argList) {
        Arrays.stream(args).forEach(v -> argList.add(v));
        return argList.toArray(new String[]{});
    }

}
