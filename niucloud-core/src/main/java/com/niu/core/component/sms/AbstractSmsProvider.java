package com.niu.core.component.sms;

import com.niu.core.component.context.SpringContext;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSmsProvider {

    // 应用的短信名称
    protected String name;

    // 设置参数
    protected Map<String, Object> params = new HashMap<>();

    // 页面组件
    protected String component;

    /**
     * 获取ApplicationContext
     * @return
     */
    protected ApplicationContext getApplicationContext(){
        return SpringContext.get();
    }

    /**
     * 设置名称
     */
    public abstract void setName();

    /*
     * 设置参数
     */
    public abstract void setParams();

    /*
     * 设置组件
     */
    public abstract void setComponent();

}
