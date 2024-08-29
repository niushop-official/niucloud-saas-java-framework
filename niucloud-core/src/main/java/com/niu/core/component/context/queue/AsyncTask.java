package com.niu.core.component.context.queue;

import java.util.HashMap;
import java.util.Map;

public final class AsyncTask {

    private int beanType;

    private Class<?> beanClass;

    private String beanName;

    private String methodName;

    private Map<String, Object> methodParamsMap = new HashMap<>();

    public AsyncTask(String beanName) {
        this.beanType = 1;
        this.beanName = beanName;
    }

    public AsyncTask(Class<?> beanClass) {
        this.beanType = 2;
        this.beanClass = beanClass;
    }

    /**
     * @param key
     * @param value
     */
    public AsyncTask addMethodParameter(String key, Object value) {
        methodParamsMap.put(key, value);
        return this;
    }

    public int getBeanType() {
        return beanType;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public AsyncTask setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Map<String, Object> getMethodParamsMap() {
        return methodParamsMap;
    }

}
