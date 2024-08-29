package com.niu.core.component.initialize.manager;

import cn.hutool.core.util.StrUtil;
import com.niu.core.component.initialize.InitializeProvider;
import com.niu.core.component.initialize.annotation.Initializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class InitializeProviderManager {

    /**
     * 初始化执行
     *
     * @param annotationClass
     * @param annotationImplClassList
     */
    public static synchronized void exec(Class<? extends Initializer> annotationClass, List<Class> annotationImplClassList, ApplicationContext applicationContext) {
        if (annotationImplClassList == null) {
            return;
        }
        Map<String, InitializeProvider> initializeProviderMap = new HashMap<>();
        for (Class annotationImplClass : annotationImplClassList) {
            Initializer initializer = (Initializer) annotationImplClass.getAnnotation(annotationClass);
            if (initializer != null) {
                String value = initializer.value();
                if (StrUtil.isNotEmpty(value)) {
                    try {
                        InitializeProvider initializeProvider = (InitializeProvider) annotationImplClass.newInstance();
                        initializeProvider.init(applicationContext);
                        initializeProviderMap.put(value, initializeProvider);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        log.error("[" + value + "]初始化错误:" + e.getMessage());
                    }
                }
            }
        }
        log.info("初始化已完成.");
        log.info("initializeProviderMap = " + initializeProviderMap);
    }

}
