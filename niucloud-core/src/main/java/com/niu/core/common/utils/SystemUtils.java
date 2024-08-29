package com.niu.core.common.utils;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.common.utils.language.LanguageUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SystemUtils {

    // 模块名称常量
    public static final String I18N = "i18n";
    public static final String LOADER = "loader";

    private static final SystemUtils systemUtils = new SystemUtils();

    private Map<String, Object> moduleMap = new HashMap<>();

    /**
     * 系统启动时注册
     */
    private SystemUtils() {
        moduleMap.put(I18N, new LanguageUtils());
        moduleMap.put(LOADER, new JsonModuleLoader());
    }

    /**
     * @param moduleName
     * @param <T>
     * @return
     */
    private <T> T getModule(String moduleName) {
        return (T) moduleMap.get(moduleName);
    }

    /**
     * 单例
     *
     * @return
     */
    private static SystemUtils getInstance() {
        return systemUtils;
    }

    /**
     * 获取系统的模块名称
     *
     * @return
     */
    public static Set<String> getDriverNames() {
        return getInstance().moduleMap.keySet();
    }

    /**
     * 获取系统模块
     *
     * @param driver
     * @param <T>
     * @return
     */
    public static <T> T getDriver(String driver) {
        return getInstance().getModule(driver);
    }

    /**
     * 通过反射获取一个类
     *
     * @param className
     * @param <T>
     * @return
     */
    public static <T> T forName(String className) {
        T target = null;
        try {
            Class<?> clazz = Class.forName(className);
            target = (T) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

    /**
     * eg，Looking downwards
     */
    public static void test() {
        // 模块名称获取
        Set<String> driverNames = SystemUtils.getDriverNames();
        driverNames.stream().forEach(name -> System.out.println(name));
        // loader模块获取使用
        JsonModuleLoader jsonModuleLoader = SystemUtils.getDriver(SystemUtils.LOADER);
        JSONArray jsonArray = jsonModuleLoader.mergeContentToArray("account_type");
        JsonModuleLoader jsonModuleLoader2 = SystemUtils.getDriver("loader");
        // 语言包获取使用
        LanguageUtils languageUtils = SystemUtils.getDriver(SystemUtils.I18N);
        String coreTestMessage = languageUtils.getMessage("core.test");
        LanguageUtils languageUtils2 = SystemUtils.getDriver("i18n");
    }

    /**
     * 是否是windows环境
     *
     * @return
     */
    public static boolean isWindowsOS() {
        String osName = getOSName();
        if (osName.contains("windows")) {
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public static boolean isLinuxOS() {
        String osName = getOSName();
        if (osName.contains("linux")) {
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 重启
     */
    public static void restart() {
        try {
            if (isWindowsOS()) {
                String command = System.getProperty("user.dir") + "/restart.bat";
                Runtime.getRuntime().exec(command);
            } else if (isLinuxOS()) {
                String command = System.getProperty("user.dir") + "/restart.sh";
                Runtime.getRuntime().exec(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
