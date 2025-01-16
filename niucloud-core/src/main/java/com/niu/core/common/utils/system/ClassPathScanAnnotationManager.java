package com.niu.core.common.utils.system;

import com.niu.core.common.config.GlobalConfig;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 从classpath下统一加载注解
 */
public final class ClassPathScanAnnotationManager {


    /**
     * @param basePackage
     * @param scanAnnotations
     * @return
     */
    public static Map<Class<? extends Annotation>, List<Class>> scanClassAnnotation(String basePackage, Class<? extends Annotation>... scanAnnotations) {
        return scanClassAnnotation(basePackage, Arrays.asList(scanAnnotations));
    }


    /**
     * @param basePackage
     * @param scanAnnotationClassList
     * @return
     */
    public static synchronized Map<Class<? extends Annotation>, List<Class>> scanClassAnnotation(String basePackage, List<Class<? extends Annotation>> scanAnnotationClassList) {
        Map<Class<? extends Annotation>, List<Class>> annotationClassListMap = new HashMap<>();
        ClassPathScanningCandidateComponentProvider componentProvider = new InnerClassPathScanningCandidateComponentProvider(false);
        scanAnnotationClassList.stream().forEach(annotationClass -> {
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        });
        componentProvider.findCandidateComponents(basePackage).forEach(beanDefinition -> {
            String beanClassName = beanDefinition.getBeanClassName();
            try {
                Class<?> beanClass = null;
                if (GlobalConfig.runActive.equals("dev")) {
                    beanClass = Class.forName(beanClassName);
                } else {
                    beanClass = Thread.currentThread().getContextClassLoader().loadClass(beanClassName);
                }
                for (Class scanAnnotationClass : scanAnnotationClassList) {
                    Annotation annotation = beanClass.getAnnotation(scanAnnotationClass);
                    if (annotation != null) {
                        List<Class> annotationClassList = annotationClassListMap.get(scanAnnotationClass);
                        if (annotationClassList == null) {
                            annotationClassList = new ArrayList<>();
                        }
                        annotationClassList.add(beanClass);
                        annotationClassListMap.put(scanAnnotationClass, annotationClassList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return annotationClassListMap;
    }

    static class InnerClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

        public InnerClassPathScanningCandidateComponentProvider() {
            this(false);
        }

        public InnerClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
            super(useDefaultFilters);
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return true;
        }
    }

}