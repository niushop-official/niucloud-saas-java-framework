package com.niu.core.common.config.executable.initialize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置初始化切入
 */
@Configuration
@Slf4j
public class ResourceInitBeanConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("afterPropertiesSet() begin.");

        log.info("afterPropertiesSet() ended.");
    }

}
