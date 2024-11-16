package com.niu.core.common.component.loader.factory;

import com.niu.core.common.component.loader.ILoaderProvider;
import com.niu.core.common.component.loader.impl.LoaderProviderImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LoaderProviderFactory {

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
    static ILoaderProvider getLoaderProvider() {
        return new LoaderProviderImpl();
    }

    /**
     * @return
     */
    public static ILoaderProvider get() {
        return new LoaderProviderImpl();
    }

}
