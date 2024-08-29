package com.niu.app.common.context;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 *
 */
public final class SpringBean {

    public static Object dynamicRegister(Class clazz, String beanName) {
        return dynamicRegister(SpringContext.get(), clazz, beanName);
    }

    /**
     * @param applicationContext
     * @param clazz
     * @param beanName
     * @return
     */
    public static Object dynamicRegister(ApplicationContext applicationContext, Class clazz, String beanName) {
        return dynamicRegister(applicationContext, clazz, beanName, null);
    }

    /**
     * @param applicationContext
     * @param clazz
     * @param beanName
     * @return
     */
    public static Object dynamicRegister(ApplicationContext applicationContext, Class clazz, String beanName, List<Object> constructorArgList) {
        DefaultListableBeanFactory autowireCapableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
        //默认单例
        beanDefinitionBuilder.setScope("prototype");
        if (constructorArgList != null && constructorArgList.size() > 0) {
            for (Object constructorArg : constructorArgList) {
                beanDefinitionBuilder.addConstructorArgValue(constructorArg);
            }
        }
        autowireCapableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        return applicationContext.getBean(beanName);
    }

}
