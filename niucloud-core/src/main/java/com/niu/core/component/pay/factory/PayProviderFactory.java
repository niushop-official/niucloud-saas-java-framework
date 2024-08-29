package com.niu.core.component.pay.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.component.pay.IPayProvider;
import com.niu.core.component.pay.annotation.PayProvider;
import com.niu.core.component.upload.IUploadProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class PayProviderFactory {

    // 对应关系模型容器
    private static Map<String, Class<? extends IPayProvider>> payProviderMap = new HashMap<>();
    private static Map<String, Set<String>> payProviderNameMap = new HashMap<>();

    /**
     * 初始化执行
     *
     * @param annotationClass
     * @param annotationImplClassList
     */
    public static synchronized void init(Class<? extends PayProvider> annotationClass, List<Class> annotationImplClassList) {
        if (annotationClass == null || annotationImplClassList == null || annotationImplClassList.size() <= 0) {
            return;
        }
        for (Class annotationImplClass : annotationImplClassList) {
            PayProvider payProvider = (PayProvider) annotationImplClass.getAnnotation(annotationClass);
            if (payProvider != null) {
                String source = payProvider.source();
                String name = payProvider.name();
                if (StrUtil.isNotEmpty(name) && StrUtil.isNotEmpty(source)) {
                    try {
                        payProviderMap.put(name, annotationImplClass);
                        Set<String> nameSet = payProviderNameMap.get(source);
                        if (nameSet == null) {
                            nameSet = new HashSet<>();
                        }
                        nameSet.add(name);
                        payProviderNameMap.put(source, nameSet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("init() 初始化已完成.");
        log.info("payProviderMap = " + payProviderMap);
    }

    /**
     * @param name
     * @return
     */
    static Class<? extends IPayProvider> getPayProvider(String name) {
        return payProviderMap.get(name);
    }

    /**
     * 返回名称与实现的对应关系
     *
     * @return
     */
    static Map<String, Class<? extends IPayProvider>> getPayProviderMap() {
        return payProviderMap;
    }

    /**
     * 返回插件与名称的对应关闭
     *
     * @return
     */
    static Map<String, Set<String>> getPayProviderNameMap() {
        return payProviderNameMap;
    }

    /**
     * @param name
     * @return
     */
    public static IPayProvider create(String name) {
        Class<? extends IPayProvider> payProviderClass = payProviderMap.get(name);
        IPayProvider payProvider = null;
        if (payProviderClass != null) {
            try {
                payProvider = payProviderClass.newInstance();
            } catch (Exception e) {
                // ignore
            }
        }
        return payProvider;
    }

}
