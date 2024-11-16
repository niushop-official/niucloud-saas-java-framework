package com.niu.core.common.component.upload.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.annotation.UploadProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class UploadProviderFactory {

    // 对应关系模型容器
    private static Map<String, Class<? extends IUploadProvider>> uploadProviderMap = new HashMap<>();
    private static Map<String, Set<String>> uploadProviderNameMap = new HashMap<>();

    /**
     * 初始化执行
     *
     * @param annotationClass
     * @param annotationImplClassList
     */
    public static synchronized void init(Class<? extends UploadProvider> annotationClass, List<Class> annotationImplClassList) {
        if (annotationClass == null || annotationImplClassList == null || annotationImplClassList.size() <= 0) {
            return;
        }
        for (Class annotationImplClass : annotationImplClassList) {
            UploadProvider uploadProvider = (UploadProvider) annotationImplClass.getAnnotation(annotationClass);
            if (uploadProvider != null) {
                String source = uploadProvider.source();
                String name = uploadProvider.name();
                if (StrUtil.isNotEmpty(name) && StrUtil.isNotEmpty(source)) {
                    try {
                        uploadProviderMap.put(name, annotationImplClass);
                        Set<String> nameSet = uploadProviderNameMap.get(source);
                        if (nameSet == null) {
                            nameSet = new HashSet<>();
                        }
                        nameSet.add(name);
                        uploadProviderNameMap.put(source, nameSet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("init() 初始化已完成.");
        log.info("uploadProviderMap = " + uploadProviderMap);
    }


    /**
     * 返回名称与实现的对应关系
     *
     * @return
     */
    static Map<String, Class<? extends IUploadProvider>> getUploadProviderMap() {
        return uploadProviderMap;
    }

    /**
     * 返回插件与名称的对应关系
     *
     * @return
     */
    static Map<String, Set<String>> getUploadProviderNameMap() {
        return uploadProviderNameMap;
    }

    /**
     * @param name
     * @return
     */
    public static IUploadProvider create(String name) {
        Class<? extends IUploadProvider> uploadProviderClass = uploadProviderMap.get(name);
        IUploadProvider uploadProvider = null;
        if (uploadProviderClass != null) {
            try {
                uploadProvider = uploadProviderClass.newInstance();
            } catch (Exception e) {
                // ignore
            }
        }
        return uploadProvider;
    }

    /**
     * 创建并初始化
     *
     * @param name
     * @param configObject
     * @return
     */
    public static IUploadProvider createAndInit(String name, JSONObject configObject) {
        IUploadProvider uploadProvider = create(name);
        if (uploadProvider != null) {
            uploadProvider.init(configObject);
        }
        return uploadProvider;
    }

}
