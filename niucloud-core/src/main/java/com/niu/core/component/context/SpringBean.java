package com.niu.core.component.context;

import org.springframework.context.MessageSource;

/**
 * 这里定义系统共用的Bean，全局直接调用
 */
public class SpringBean {

    /**
     * 获取国际化源对象
     */
    public static MessageSource getCoreMessageSource() {
        MessageSource messageSource = (MessageSource) SpringContext.bean("coreMessageSource");
        return messageSource;
    }


}
