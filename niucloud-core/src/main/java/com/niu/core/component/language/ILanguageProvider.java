package com.niu.core.component.language;

import java.util.Locale;

public interface ILanguageProvider {

    /**
     * 根据Key获取国际化消息
     *
     * @param key
     * @return
     */
    public String getMessage(String key);

    /**
     * 根据Key和Locale获取国际化消息
     *
     * @param key
     * @param locale
     * @return
     */
    public String getMessage(String key, Locale locale);

}
