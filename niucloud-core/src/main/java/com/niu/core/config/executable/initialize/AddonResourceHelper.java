package com.niu.core.config.executable.initialize;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.SpringModuleApplication;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.component.context.WebAppEnvs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class AddonResourceHelper {

    /**
     * 动态搜索插件注解
     *
     * @return
     */
    public static Map<String, String> loadAddonEnvsMapByScan() {
        Map<String, String> addonEnvsMap = loadAddonEnvsMapByScan(new HashMap<>());
        log.info("addonEnvsMap = " + addonEnvsMap);
        if (addonEnvsMap != null && !addonEnvsMap.containsKey("core")) {
            addonEnvsMap.put("core", "com.niu.core.WebAppApplication");
        }
        return addonEnvsMap;
    }

    /**
     * 动态搜索插件注解
     *
     * @param envsMap
     * @return
     */
    private static Map<String, String> loadAddonEnvsMapByScan(Map<String, String> envsMap) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return true;
            }
        };
        provider.addIncludeFilter(new AnnotationTypeFilter(SpringModuleApplication.class)); // 自定义注解的类型过滤器
        provider.findCandidateComponents("com.niu").forEach(beanDefinition -> {
            String beanClassName = beanDefinition.getBeanClassName();
            try {
                Class<?> beanClass = Class.forName(beanClassName);
                SpringModuleApplication springModuleApplication = beanClass.getAnnotation(SpringModuleApplication.class);
                envsMap.put(springModuleApplication.value(), beanClassName);
                log.info("loadAddonEnvsMapByScan()---" + springModuleApplication.value() + "," + beanClassName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return envsMap;
    }

    /**
     * 读取addon.json文件，获取到插件列表以map的形式返回
     *
     * @return
     */
    public static Map<String, String> loadDynamicAddon() {
        Map<String, String> envsMap = new HashMap<>();
        String addonConfig = WebAppEnvs.get().webRootDownJar + "/addon/addon.json";
        String jsonString = JsonLoadUtils.loadJsonString(new File(addonConfig));
        if (StrUtil.isNotEmpty(jsonString)) {
            try {
                JSONArray jsonArray = JSONUtil.parseArray(jsonString);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject entityJson = jsonArray.getJSONObject(i);
                        String name = entityJson.getStr("name");
                        String mainClass = entityJson.getStr("mainClass");
                        if (StrUtil.isNotEmpty(name) && StrUtil.isNotEmpty(mainClass)) {
                            envsMap.put(name, mainClass);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return envsMap;
    }

}
