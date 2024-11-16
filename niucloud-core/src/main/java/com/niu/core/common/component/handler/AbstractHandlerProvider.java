package com.niu.core.common.component.handler;

import com.niu.core.common.component.context.SpringContext;
import org.springframework.context.ApplicationContext;

public abstract class AbstractHandlerProvider {

    /**
     * 获取ApplicationContext
     * @return
     */
    protected ApplicationContext getApplicationContext(){
        return SpringContext.get();
    }

}
