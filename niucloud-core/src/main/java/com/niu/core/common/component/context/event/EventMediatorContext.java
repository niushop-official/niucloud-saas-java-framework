package com.niu.core.common.component.context.event;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.example.CoreSourceEvent;
import com.niu.core.event.example.DemoEventDefiner;
import com.niu.core.event.example.ExampleEventDefiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 业务事件监听及事件回调组件初始化
 */
@Slf4j
public class EventMediatorContext {

    /**
     * 初始化
     */
    public static void init() {
        String basePackage = "com";
        List<Class> eventCallbackClassList = scanAnnotationByComponentProvider(basePackage, EventCallback.class);
        CallbackMediator.getInstance().init(eventCallbackClassList);
//        log.info("eventCallbackClassList = " + eventCallbackClassList);
    }

    /**
     * 测试
     */
    public static void test() {
        System.out.println("↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔");
        CoreSourceEvent businessEvent = new CoreSourceEvent();
        businessEvent.setSiteId(1);
        businessEvent.addAppSign("core");
        businessEvent.addAppSign("shop");
        EventPublisher.publish(businessEvent);
        System.out.println("↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔");
        DemoEventDefiner.DemoEvent demoEvent = new DemoEventDefiner.DemoEvent();
        demoEvent.setAuthority(true);
        demoEvent.addAppSigns("core", "shop");
        Map<String, EventResult> publishResult = CallbackPublisher.publish(demoEvent);
        System.out.println("↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔");
        System.out.println("publishResult = " + publishResult);
        System.out.println("publishResult name = " + publishResult.get("core").getName());
        System.out.println("↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔");
        ExampleEventDefiner.ExampleEvent exampleEvent = new ExampleEventDefiner.ExampleEvent();
        exampleEvent.setAuthority(true);
        exampleEvent.addAppSigns("core", "shop");
        Map<String, EventResult> publishExampleResult = CallbackPublisher.publish(exampleEvent);
        System.out.println("↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔");
        System.out.println("publishExampleResult = " + publishExampleResult);
        System.out.println("publishResult name = " + publishResult.get("core").getName());
        System.out.println("↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔↔");
    }

    /**
     * 动态搜索插件注解
     *
     * @param basePackage
     * @param annotationClass
     * @return
     */
    private static List<Class> scanAnnotationByComponentProvider(String basePackage, Class<? extends Annotation> annotationClass) {
        List<Class> classList = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        // 自定义注解的类型过滤器
        provider.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        provider.findCandidateComponents(basePackage).forEach(beanDefinition -> {
            String beanClassName = beanDefinition.getBeanClassName();
            try {
                Class<?> beanClass = Class.forName(beanClassName);
                classList.add(beanClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return classList;
    }


}
