package com.niu.core.common.component.initialize;

import org.springframework.context.ApplicationContext;

public interface InitializeProvider {

    /**
     * 初始化方法
     *
     * @param applicationContext
     * @throws Exception
     */
    public void init(ApplicationContext applicationContext) throws Exception;

}
