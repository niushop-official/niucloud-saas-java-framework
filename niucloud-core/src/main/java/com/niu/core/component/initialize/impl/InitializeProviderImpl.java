package com.niu.core.component.initialize.impl;

import com.niu.core.component.initialize.InitializeProvider;
import com.niu.core.component.initialize.annotation.Initializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

@Initializer("core")
@Slf4j
public class InitializeProviderImpl implements InitializeProvider {

    @Override
    public void init(ApplicationContext applicationContext) throws Exception {
        log.warn("这里是core的实现，目的是为了测试功能的可用性.");
    }

}
