package com.niu.core.component.sms.factory;

import cn.hutool.core.util.StrUtil;
import com.niu.core.component.sms.ISmsProvider;
import com.niu.core.component.sms.annotation.SmsProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class SmsProviderFactory {

    // 对应关系模型容器
    private static Map<String, ISmsProvider> smsProviderMap = new HashMap<>();
    private static Map<String, Set<String>> smsProviderNameMap = new HashMap<>();

    /**
     * 初始化执行
     *
     * @param annotationClass
     * @param annotationImplClassList
     */
    public static synchronized void init(Class<? extends SmsProvider> annotationClass, List<Class> annotationImplClassList) {
        if (annotationClass == null || annotationImplClassList == null || annotationImplClassList.size() <= 0) {
            return;
        }
        for (Class annotationImplClass : annotationImplClassList) {
            SmsProvider smsProvider = (SmsProvider) annotationImplClass.getAnnotation(annotationClass);
            if (smsProvider != null) {
                String source = smsProvider.source();
                String name = smsProvider.name();
                if (StrUtil.isNotEmpty(name) && StrUtil.isNotEmpty(source)) {
                    try {
                        smsProviderMap.put(name, (ISmsProvider) annotationImplClass.newInstance());
                        Set<String> nameSet = smsProviderNameMap.get(source);
                        if (nameSet == null) {
                            nameSet = new HashSet<>();
                        }
                        nameSet.add(name);
                        smsProviderNameMap.put(source, nameSet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("init() 初始化已完成.");
        log.info("smsProviderMap = " + smsProviderMap);
    }

    /**
     * @param name
     * @return
     */
    static ISmsProvider getSmsProvider(String name) {
        return smsProviderMap.get(name);
    }

    /**
     * 返回名称与实现的对应关系
     *
     * @return
     */
    static Map<String, ISmsProvider> getSmsProviderMap() {
        return smsProviderMap;
    }

    /**
     * 返回插件与名称的对应关闭
     *
     * @return
     */
    static Map<String, Set<String>> getSmsProviderNameMap() {
        return smsProviderNameMap;
    }

    /**
     * @param name
     * @return
     */
    public static ISmsProvider get(String name) {
        return smsProviderMap.get(name);
    }

}
