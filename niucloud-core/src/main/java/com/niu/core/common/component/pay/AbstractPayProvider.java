package com.niu.core.common.component.pay;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.pay.annotation.PayProvider;
import org.springframework.context.ApplicationContext;

public abstract class AbstractPayProvider {

    // 支付名称
    protected String name;

    // 支付Key
    protected String key;

    // 支付图标
    protected String icon;

    // 页面组件
    protected String settingComponent;

    /**
     * 获取ApplicationContext
     *
     * @return
     */
    protected ApplicationContext getApplicationContext() {
        return SpringContext.get();
    }

    /**
     * 获取当前类的注解
     *
     * @return
     */
    protected PayProvider getPayProvider() {
        return this.getClass().getAnnotation(PayProvider.class);
    }

    /**
     * 获取注解（PayProvider）的上传方式名称（name）
     *
     * @return
     */
    public String getName() {
        if (getPayProvider() != null) {
            return getPayProvider().name();
        }
        return "";
    }

    /*
     * 获取注解（PayProvider）的上传方式名称（name）
     * 设置参数
     */
    public String getKey() {
        return this.getName();
    }

    /*
     * 获取插件名称
     */
    public String getSource() {
        if (getPayProvider() != null) {
            return getPayProvider().source();
        }
        return "";
    }

    /*
     * 设置参数
     */
    public String getIcon() {
        if (getPayProvider() != null) {
            return getPayProvider().icon();
        }
        return "";
    }

    public String getDescribe() {
        if (getPayProvider() != null) {
            return getPayProvider().describe();
        }
        return "";
    }

    /*
     * 设置组件
     */
    public String getSettingComponent() {
        if (getPayProvider() != null) {
            return getPayProvider().settingComponent();
        }
        return "";
    }

}
