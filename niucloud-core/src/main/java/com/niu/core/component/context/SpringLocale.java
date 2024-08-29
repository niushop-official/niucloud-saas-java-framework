package com.niu.core.component.context;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class SpringLocale {

    /**
     * 根据Key获取zh_CN的内容 <>messages_zh_CN.properties</>
     *
     * @param key
     * @return
     */
    public static String getZhCnMessage(String key) {
        return getMessage(key, Locale.SIMPLIFIED_CHINESE);
    }

    /**
     * 根据Key获取en_US的内容 <>messages_en_US.properties</>
     *
     * @param key
     * @return
     */
    public static String getEnUsMessage(String key) {
        return getMessage(key, Locale.US);
    }

    /**
     * 根据客户端请求构建的Local来获取内容
     *
     * @param key
     * @return
     */
    public static String getRequestDefaultMessage(String key) {
        return getMessage(key, LocaleContextHolder.getLocale());
    }

    /**
     * 指定key、local来获取
     *
     * @param key
     * @param Locale
     * @return
     */
    public static String getMessage(String key, Locale Locale) {
        return SpringBean.getCoreMessageSource().getMessage(key, null, Locale);
    }

    /**
     * 指定构建的Bean名称、key、local来获取
     *
     * @param messageSourceBeanName
     * @param key
     * @param Locale
     * @return
     */
    public static String getMessage(String messageSourceBeanName, String key, Locale Locale) {
        MessageSource messageSource = (MessageSource) SpringContext.bean(messageSourceBeanName);
        return messageSource.getMessage(key, null, Locale);
    }

}
