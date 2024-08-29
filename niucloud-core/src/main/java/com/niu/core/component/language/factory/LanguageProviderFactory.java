package com.niu.core.component.language.factory;

import com.niu.core.component.language.ILanguageProvider;
import com.niu.core.component.language.impl.LanguageProviderImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LanguageProviderFactory {

    /**
     * 初始化
     */
    public static synchronized void init() {
        // 暂无初始化
        log.info("init() 初始化已完成.");
    }

    /**
     * @return
     */
    public static ILanguageProvider getLanguageProvider() {
        return new LanguageProviderImpl();
    }

}
