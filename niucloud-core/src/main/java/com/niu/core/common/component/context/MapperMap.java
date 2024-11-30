package com.niu.core.common.component.context;

import com.niu.core.common.config.dataization.AddonModuleContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Slf4j
public final class MapperMap {

    private static final Map<String, List<String>> mappersByModule = new LinkedHashMap<>();

    public static synchronized void init(Class<? extends Mapper> annotationClass, List<Class> annotationImplClassList) {
        if (annotationClass == null || annotationImplClassList == null || annotationImplClassList.size() <= 0) {
            return;
        }

        Map<String, String> addonModuleMap = getAddonModuleMap();

        for (Class annotationImplClass : annotationImplClassList) {
            Mapper mapper = (Mapper) annotationImplClass.getAnnotation(annotationClass);
            if (mapper != null) {
                String moduleName = getModuleName(addonModuleMap, annotationImplClass.getName());
                List<String> mappers = mappersByModule.getOrDefault(moduleName, new LinkedList<>());
                mappers.add(annotationImplClass.getName());
                mappersByModule.put(moduleName, mappers);
            }
        }
        log.info("mappersByModule = " + mappersByModule);
    }

    private static String getModuleName(Map<String, String> addonModuleMap, String className) {
        String module = "core";
        for (String key : addonModuleMap.keySet()) {
            if (className.startsWith(key + ".")) {
                module = addonModuleMap.get(key);
                break;
            }
        }
        return module;
    }

    private static Map<String, String> getAddonModuleMap() {
        Map<String, String> map = new HashMap<>();

        Map<String, Class<?>> addonModule = AddonModuleContext.getAddonModuleMap();
        for (String key: addonModule.keySet()) {
            Class clazz = addonModule.get(key);
            map.put(clazz.getPackage().getName(), key);
        }

        return map;
    }

    public static List<String> getMappersByModule(String module) {
        return mappersByModule.getOrDefault(module, new LinkedList<>());
    }
}
