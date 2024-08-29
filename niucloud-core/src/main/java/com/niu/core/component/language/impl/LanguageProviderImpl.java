package com.niu.core.component.language.impl;

import com.niu.core.component.language.ILanguageProvider;
import com.niu.core.common.utils.language.AddonModuleBundleMessageSource;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class LanguageProviderImpl implements ILanguageProvider {

    @Override
    public String getMessage(String key) {
        return AddonModuleBundleMessageSource.getMessage(key);
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return AddonModuleBundleMessageSource.getMessage(key, locale);
    }

}
