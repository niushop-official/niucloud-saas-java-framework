package com.niu.core.component.handler.factory;

import cn.hutool.core.util.StrUtil;
import com.niu.core.component.context.SpringDynamicBean;
import com.niu.core.component.handler.IHandlerProvider;
import com.niu.core.component.handler.annotation.HandlerProvider;
import com.niu.core.component.context.SpringContext;
import com.niu.core.component.context.task.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public final class HandlerProviderFactory {

    // 对应关系模型容器
    private static Map<Class<?>, Set<Class<? extends IHandlerProvider>>> handlerProviderMap = new HashMap<>();
    private static Map<String, Set<String>> handlerProviderClassMap = new HashMap<>();
    private static Map<String, Set<String>> handlerProviderModelMap = new HashMap<>();
    // 调用的模板方法名称
    private static final String HANDLE_METHOD = "handle";

    /**
     * 初始化执行
     *
     * @param annotationClass
     * @param annotationImplClassList
     */
    public static synchronized void init(Class<? extends HandlerProvider> annotationClass, List<Class> annotationImplClassList) {
        if (annotationClass == null || annotationImplClassList == null || annotationImplClassList.size() <= 0) {
            return;
        }
        for (Class annotationImplClass : annotationImplClassList) {
            HandlerProvider handlerProvider = (HandlerProvider) annotationImplClass.getAnnotation(annotationClass);
            if (handlerProvider != null) {
                String name = handlerProvider.value();
                if (StrUtil.isNotEmpty(name)) {
                    try {
                        // 根据handle方法参数类型装入map
                        Class argOneClass = handleMethodArgOneClass(annotationImplClass);
                        if (argOneClass == null) {
                            continue;
                        }
                        Set<Class<? extends IHandlerProvider>> handlerProviderClassSet = handlerProviderMap.get(argOneClass);
                        if (handlerProviderClassSet == null) {
                            handlerProviderClassSet = new HashSet<>();
                        }
                        handlerProviderClassSet.add(annotationImplClass);
                        handlerProviderMap.put(argOneClass, handlerProviderClassSet);
                        // 根据插件名称装入实现类
                        Set<String> handlerProviderNameSet = handlerProviderClassMap.get(name);
                        if (handlerProviderNameSet == null) {
                            handlerProviderNameSet = new HashSet<>();
                        }
                        handlerProviderNameSet.add(annotationImplClass.getName());
                        handlerProviderClassMap.put(name, handlerProviderNameSet);
                        // 根据插件名称装入调用参数的模型类
                        Set<String> handlerProviderModelSet = handlerProviderModelMap.get(name);
                        if (handlerProviderModelSet == null) {
                            handlerProviderModelSet = new HashSet<>();
                        }
                        handlerProviderModelSet.add(argOneClass.getName());
                        handlerProviderModelMap.put(name, handlerProviderModelSet);
                        // 手动注入
                        SpringDynamicBean.dynamicRegister(annotationImplClass, annotationImplClass.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("init() 初始化已完成.");
        log.info("handlerProviderMap = " + handlerProviderMap);
    }

    /**
     * @param clazz
     * @return
     */
    private static Class handleMethodArgOneClass(Class<? extends IHandlerProvider> clazz) {
        Class paramClass = null;
        Method[] methods = clazz.getMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                if (HANDLE_METHOD.equals(method.getName()) && method.getParameterCount() == 1) {
                    paramClass = method.getParameterTypes()[0];
                    break;
                }
            }
        }
        return paramClass;
    }

    /**
     * 返回调用对象与具体实现的对应关系
     *
     * @return
     */
    static Map<Class<?>, Set<Class<? extends IHandlerProvider>>> getHandlerProviderMap() {
        return handlerProviderMap;
    }

    /**
     * 返回插件与实现的对应关系
     *
     * @return
     */
    static Map<String, Set<String>> getHandlerProviderClassMap() {
        return handlerProviderClassMap;
    }

    /**
     * 返回插件与名称的对应关系
     *
     * @return
     */
    static Map<String, Set<String>> getHandlerProviderModelMap() {
        return handlerProviderModelMap;
    }

    /**
     * 同步调用
     *
     * @return
     */
    public static <R> List<R> invoke(Object bean) {
        Set<Class<? extends IHandlerProvider>> handlerProviderClassSet = handlerProviderMap.get(bean.getClass());
        List<R> resultList = new ArrayList<>();
        if (handlerProviderClassSet != null && handlerProviderClassSet.size() > 0) {
            for (Class handlerProviderClass : handlerProviderClassSet) {
                // 通过Spring来完成调用
                IHandlerProvider iHandlerProvider = (IHandlerProvider) SpringContext.bean(handlerProviderClass);
                R result = (R) iHandlerProvider.handle(bean);
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 异步调用
     *
     * @param bean
     */
    public static void asyncInvoke(Object bean) {
        ThreadPoolManager.runTask(() -> {
            invoke(bean);
        });
    }

}
