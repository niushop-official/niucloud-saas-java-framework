package com.niu.core.common.utils.language;

import java.util.Locale;

public class LanguageUtils {

    public String getMessage(String key) {
        return AddonModuleBundleMessageSource.getMessage(key);
    }

    public String getMessage(String key, Locale locale) {
        return AddonModuleBundleMessageSource.getMessage(key, locale);
    }

    public static String get(String key) {
        return AddonModuleBundleMessageSource.getMessage(key);
    }

    public static String get(String key, Locale locale) {
        return AddonModuleBundleMessageSource.getMessage(key, locale);
    }

}
