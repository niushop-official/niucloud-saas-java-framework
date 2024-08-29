package com.niu.app.common.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.Assert;

public class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {

    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        return beanClassName;
    }

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String shortClassName = super.generateBeanName(definition, registry);
        // 如果存在相同名称的bean，则将要注册的bean的名称设置为全路径名
        if (registry.containsBeanDefinition(shortClassName)) {
            return definition.getBeanClassName();
        }
        return shortClassName;
    }

}
