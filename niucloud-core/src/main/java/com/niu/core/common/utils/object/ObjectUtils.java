package com.niu.core.common.utils.object;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.reflect.ReflectCallMethod;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 对象处理工具类
 */
public class ObjectUtils {

    /**
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> tClass) {
        if (tClass != null) {
            try {
                return tClass.newInstance();
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * @param tClass
     * @param parameterMap
     * @param <T>
     * @return
     */
    public static <T> T build(Class<T> tClass, Map<String, String[]> parameterMap) {
        return build(null, tClass, parameterMap);
    }

    /**
     * @param target
     * @param tClass
     * @param parameterMap
     * @param <T>
     * @return
     */
    public static <T> T build(T target, Class<T> tClass, Map<String, String[]> parameterMap) {
        if (target == null) {
            try {
                target = tClass.newInstance();
            } catch (Exception e) {
                // ignore
            }
        }
        Field[] fields = target.getClass().getDeclaredFields();
        if (fields != null) {
            Object finalTarget = target;
            Arrays.stream(fields).forEach(field -> {
                doRequestParameter(finalTarget, field, parameterMap);
            });
        }
        return null;
    }

    /**
     * @param target
     * @param field
     * @param preprocessingMap
     */
    private static void doRequestParameter(Object target, Field field, Map<String, String[]> preprocessingMap) {
        String filedName = field.getName();
        preprocessingMap.forEach((k, v) -> {
            if (filedName.equals(k)) {
                if (field.getType().equals(String[].class)) {
                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) v);
                }
            }
        });
        if (filedName.matches(".*[A-Z].*") && !"serialVersionUID".equals(filedName)) {
            String underFiledName = StringUtils.toUnderScoreCase(filedName);
            Map<String, String[]> parameterMap = RequestUtils.handler().getParameterMap();
            String[] valueObjects = parameterMap.get(underFiledName);
            if (valueObjects == null || valueObjects.length <= 0) {
                return;
            } else {
                if (field.getType().equals(String.class)) {
                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) valueObjects[0]);
                } else if (field.getType().equals(Integer.class)) {
                    try {
                        Integer value = Integer.parseInt(valueObjects[0]);
                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Long.class)) {
                    try {
                        Long value = Long.parseLong(valueObjects[0]);
                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Double.class)) {
                    try {
                        Double value = Double.parseDouble(valueObjects[0]);
                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Float.class)) {
                    try {
                        Float value = Float.parseFloat(valueObjects[0]);
                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(String[].class)) {
                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) valueObjects);
                } else if (field.getType().equals(Date.class)) {
                    // 时间暂时不处理
                } else if (field.getType().equals(LocalDateTime.class)) {
                    // 时间暂时不处理（JDK.18以上）
                }
            }
        }
    }

}
