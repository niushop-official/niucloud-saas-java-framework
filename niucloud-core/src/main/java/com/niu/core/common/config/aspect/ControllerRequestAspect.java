package com.niu.core.common.config.aspect;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.reflect.ReflectCallMethod;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class ControllerRequestAspect {

    // 定义切点，匹配所有Controller中的方法
    @Pointcut("execution(* com..*.controller..*.*(..))")
    public void controllerMethods() {
    }

    // 前置通知
    @Before("controllerMethods()")
    public void beforeControllerMethod(JoinPoint joinPoint) {
        Object currentObject = joinPoint.getTarget();
        log.debug("Before: " + currentObject + "->" + joinPoint.getSignature().getName() + "()");
        final Map<String, String[]> preprocessingMap = this.receiveRequestArrayParameter();
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            log.debug("=================================参数: " + arg);
            Field[] fields = arg.getClass().getDeclaredFields();
            if (fields != null) {
                Arrays.stream(fields).forEach(field -> {
                    this.doRequestParameter(arg, field, preprocessingMap);
                });
            }
            log.debug("Request请求参数: " + arg);
        }
    }

    /**
     * @return
     */
    private Map<String, String[]> receiveRequestArrayParameter() {
        Map<String, String[]> preprocessingMap = new HashMap<>();
        Map<String, String[]> parameterMap = RequestUtils.handler().getParameterMap();
        Set<String> parameterSet = parameterMap.keySet();
        if (parameterSet != null && parameterSet.size() > 0) {
            parameterSet.stream().forEach(key -> {
                if (key.endsWith("[]")) {
                    String[] values = parameterMap.get(key);
                    if (values != null && values.length > 0) {
                        if (values.length == 1) {
                            String value = values[0];
                            String[] arrays = value.split(",");
                            String newKey = key.replace("[]", "");
                            newKey = StringUtils.toCamelCase(newKey);
                            preprocessingMap.put(newKey, arrays);
                        } else {
                            String[] arrays = values;
                            String newKey = key.replace("[]", "");
                            newKey = StringUtils.toCamelCase(newKey);
                            preprocessingMap.put(newKey, arrays);
                        }
                    }
                }
            });
        }
        return preprocessingMap;
    }

    /**
     * @param target
     * @param field
     * @param preprocessingMap
     */
    private void doRequestParameter(Object target, Field field, Map<String, String[]> preprocessingMap) {
        String filedName = field.getName();
        preprocessingMap.forEach((k, v) -> {
            if (filedName.equals(k)) {
                if (field.getType().equals(String[].class)) {
                    ReflectCallMethod.setFieldValue(target, field, v);
//                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) v);
                } else if (field.getType().equals(List.class)) {
                    // 获取泛型类型
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listGenericType = (Class<?>) listType.getActualTypeArguments()[0];

                    if (listGenericType.equals(Integer.class)) {
                        List<Integer> intList = Arrays.stream(v)
                                .filter(str -> str != null && !str.trim().isEmpty())
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        ReflectCallMethod.setFieldValue(target, field, intList);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, intList);
                    } else if (listGenericType.equals(String.class)) {
                        List<String> stringList = Arrays.stream(v)
                                .filter(str -> str != null && !str.trim().isEmpty())
                                .collect(Collectors.toList());
                        ReflectCallMethod.setFieldValue(target, field, stringList);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, stringList);
                    }
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
                    ReflectCallMethod.setFieldValue(target, field, (Object) valueObjects[0]);
//                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) valueObjects[0]);
                } else if (field.getType().equals(Integer.class)) {
                    try {
                        Integer value = Integer.parseInt(valueObjects[0]);
                        ReflectCallMethod.setFieldValue(target, field, value);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Long.class)) {
                    try {
                        Long value = Long.parseLong(valueObjects[0]);
                        ReflectCallMethod.setFieldValue(target, field, value);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Double.class)) {
                    try {
                        Double value = Double.parseDouble(valueObjects[0]);
                        ReflectCallMethod.setFieldValue(target, field, value);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(Float.class)) {
                    try {
                        Float value = Float.parseFloat(valueObjects[0]);
                        ReflectCallMethod.setFieldValue(target, field, value);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) value);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (field.getType().equals(String[].class)) {
                    ReflectCallMethod.setFieldValue(target, field, valueObjects);
//                    ReflectCallMethod.setInvokeByFieldName(target, filedName, (Object) valueObjects);
                } else if (field.getType().equals(Date.class)) {
                    // 时间暂时不处理
                } else if (field.getType().equals(LocalDateTime.class)) {
                    // 时间暂时不处理（JDK.18以上）
                } else if (field.getType().equals(List.class)) {
                    // 处理 List 类型
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listGenericType = (Class<?>) listType.getActualTypeArguments()[0];

                    if (listGenericType.equals(Integer.class)) {
                        List<Integer> intList = Arrays.stream(valueObjects)
                                .filter(str -> str != null && !str.trim().isEmpty())
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        ReflectCallMethod.setFieldValue(target, field, intList);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, intList);
                    }
                    else if (listGenericType.equals(String.class)) {
                        List<String> stringList = Arrays.stream(valueObjects)
                                .filter(str -> str != null && !str.trim().isEmpty())
                                .collect(Collectors.toList());
                        ReflectCallMethod.setFieldValue(target, field, stringList);
//                        ReflectCallMethod.setInvokeByFieldName(target, filedName, stringList);
                    }
                }
            }
        }
    }

}
