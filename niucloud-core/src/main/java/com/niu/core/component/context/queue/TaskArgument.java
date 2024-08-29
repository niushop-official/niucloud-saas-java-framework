package com.niu.core.component.context.queue;

import java.util.HashMap;

public class TaskArgument extends HashMap{

    private Object targetBean;

    private String targetMethod;

    public Object getTargetBean() {
        return targetBean;
    }

    public void setTargetBean(Object targetBean) {
        this.targetBean = targetBean;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }
}
