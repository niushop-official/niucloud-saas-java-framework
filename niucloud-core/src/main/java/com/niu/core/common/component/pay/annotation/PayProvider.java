package com.niu.core.common.component.pay.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PayProvider {

    String name();

    String source();

    String describe();

    String icon();

    String settingComponent();

}
