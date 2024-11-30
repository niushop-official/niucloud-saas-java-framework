package com.niu.core.common.component.upgrade.factory;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.annotation.UploadProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class UpgradeProviderFactory {

    private static Map<String, Class<? extends IUpgradeProvider>> upgradeProviderMap = new HashMap<>();

    /**
     * 初始化执行
     *
     * @param annotationClass
     * @param annotationImplClassList
     */
    public static synchronized void init(Class<? extends UpgradeProvider> annotationClass, List<Class> annotationImplClassList) {
        if (annotationClass == null || annotationImplClassList == null || annotationImplClassList.size() <= 0) {
            return;
        }
        for (Class annotationImplClass : annotationImplClassList) {
            UpgradeProvider upgradeProvider = (UpgradeProvider) annotationImplClass.getAnnotation(annotationClass);
            if (upgradeProvider != null) {
                String addon = upgradeProvider.addon();
                String version = upgradeProvider.version();
                if (StrUtil.isNotEmpty(addon) && StrUtil.isNotEmpty(version)) {
                    try {
                        upgradeProviderMap.put(addon + version, annotationImplClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.info("upgradeProviderMap = " + upgradeProviderMap);
    }

    /**
     * 执行升级方法
     * @param addon
     * @param version
     */
    public static void handle(String addon, String version) {
        Class<? extends IUpgradeProvider> upgradeProviderClass = upgradeProviderMap.get(addon + version);
        IUpgradeProvider uploadProvider = null;
        if (upgradeProviderClass != null) {
            try {
                uploadProvider = upgradeProviderClass.newInstance();
                uploadProvider.handle();
            } catch (InstantiationException | IllegalAccessException e) {
            }
        }
    }
}
