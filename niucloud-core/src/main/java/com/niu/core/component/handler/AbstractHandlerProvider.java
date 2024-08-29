package com.niu.core.component.handler;

import com.niu.core.component.context.SpringContext;
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
