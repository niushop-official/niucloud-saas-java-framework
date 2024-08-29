package com.niu.core.common.utils.language;

import cn.hutool.core.util.StrUtil;
import com.niu.core.config.GlobalConfig;
import com.niu.core.config.dataization.AddonModuleContext;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AddonModuleBundleMessageSource {

    static Map<String, BundleMessageSource> addonModuleBundleMap = new ConcurrentHashMap<>();

    // 消息源
    static class BundleMessageSource {

        private String addonModule;

        private Map<String, Properties> bundleMessageSourceMap = new ConcurrentHashMap<>();

        public BundleMessageSource(String addonModule) {
            this.addonModule = addonModule;
        }

        /**
         * @param relativePath
         * @param local
         */
        synchronized void load(String relativePath, String local) {
            Properties properties = null;
            boolean loadResult = false;
            try {
                properties = AddonModuleContext.readResourceAsStreamToProperties(addonModule, relativePath + "_" + local + ".properties");
                loadResult = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (loadResult) {
                log.info("国际化文件加载： " + addonModule + "==>" + relativePath + "_" + local + ".properties");
                this.bundleMessageSourceMap.put(local, properties);
            }
        }

        String getMessage(String key, Locale locale) {
            if (locale == null) {
                return "";
            }
            String localeKey = getLocaleString(locale);
            // log.info("获取： " + largeModule + " >>>> " + localeKey + " >>>> " + key);
            Properties properties = bundleMessageSourceMap.get(localeKey);
            if (properties == null) {
                properties = bundleMessageSourceMap.get(localeKey);
            }
            String value = "";
            if (properties != null) {
                value = properties.getProperty(key);
            }
            return value;
        }

        String getLocaleString(Locale locale) {
            String language = locale.getLanguage().toLowerCase();
            String country = locale.getCountry().toUpperCase();
            if (country.endsWith("_")) {
                country = country.replace("_", "");
            }
            String localeKey = language + "_" + country;
            return localeKey;
        }

        Set<String> getLanguages() {
            return bundleMessageSourceMap.keySet();
        }

    }

    /**
     * 初始化<默认初始：zh_CN、en_US>
     */
    public static void init() {
        Set<String> languageSet = new HashSet<>();
        String language = "zh_CN";
        if (StrUtil.isNotBlank(GlobalConfig.defaultLanguage)) {
            language = GlobalConfig.defaultLanguage;
        }
        languageSet.add(language);
        languageSet.add("en_US");
        init(languageSet);
    }

    /**
     * @param languageSet
     */
    public static void init(Set<String> languageSet) {
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        for (String addonModule : addonModules) {
            for (String language : languageSet) {
                refresh(addonModule, language);
            }
        }
    }

    /**
     * 刷新<>系统启动时刷新所有模块</>
     */
    public static void refresh() {
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        for (String addonModule : addonModules) {
            refresh(addonModule);
        }
    }

    /**
     * 根据插件模块以及语言刷新
     *
     * @param addonModule
     * @param language
     */
    public synchronized static void refresh(String addonModule, String language) {
        BundleMessageSource bundleMessageSource = addonModuleBundleMap.get(addonModule);
        if (bundleMessageSource == null) {
            bundleMessageSource = new BundleMessageSource(addonModule);
        }
        bundleMessageSource.load("/i18n/messages", language);
        addonModuleBundleMap.put(addonModule, bundleMessageSource);
    }

    /**
     * @param addonModule
     */
    public static void refresh(String addonModule) {
        BundleMessageSource bundleMessageSource = addonModuleBundleMap.get(addonModule);
        if (bundleMessageSource != null) {
            Set<String> languageSet = bundleMessageSource.getLanguages();
            for (String language : languageSet) {
                bundleMessageSource.load("/i18n/messages", language);
            }
        }
    }

    /**
     * @param addonModule
     * @param key
     * @param locale
     * @return
     */
    protected static String getMessage(String addonModule, String key, Locale locale) {
        BundleMessageSource bundleMessageSource = addonModuleBundleMap.get(addonModule);
        if (bundleMessageSource == null) {
            bundleMessageSource = new BundleMessageSource(addonModule);
            String localeKey = bundleMessageSource.getLocaleString(locale);
            bundleMessageSource.load("/i18n/messages", localeKey);
            addonModuleBundleMap.put(addonModule, bundleMessageSource);
        }
        return bundleMessageSource.getMessage(key, locale);
    }

    /**
     * @param addonModule
     * @param key
     * @return
     */
    protected static String getMessage(String addonModule, String key) {
        String language = "zh_CN";
        if (StrUtil.isNotBlank(GlobalConfig.defaultLanguage)) {
            language = GlobalConfig.defaultLanguage;
        }
        return getMessage(addonModule, language);
    }


    /**
     * @param key
     * @param locale
     * @return
     */
    public static String getMessage(String key, Locale locale) {
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        for (String addonModule : addonModules) {
            String message = getMessage(addonModule, key, locale);
            if (StrUtil.isNotEmpty(message)) {
                return message;
            }
        }
        return key;
    }

    /**
     * @param key
     * @return
     */
    public static String getMessage(String key) {
        Locale locale = Locale.CHINA;
        if (GlobalConfig.defaultLanguage.equalsIgnoreCase("zh_CN")) {
            locale = Locale.CHINA;
        } else if (GlobalConfig.defaultLanguage.equalsIgnoreCase("en_US")) {
            locale = Locale.US;
        }
        String message = getMessage(key, locale);
        if (!StrUtil.isEmpty(message)) {
            return message;
        }
        return key;
    }

}
