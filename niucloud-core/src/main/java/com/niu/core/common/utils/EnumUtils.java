package com.niu.core.common.utils;

import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

public class EnumUtils {

    /**
     * 通过传入枚举类的class对象，和属性值 获取对应的想要的内容字段
     * @param key 传入属性值内容
     * @param enumCalss 枚举类 class
     * @param GetEnumKey 属性值内容的get方法名称
     * @param GetEnumValue 获取返回值的value方法名称
     * @return
     */
    public static Object getValueByKey(Object key, Class<? extends Enum<?>> enumCalss, String GetEnumKey, String GetEnumValue){
        Object result=null;
        if(ObjectUtils.isEmpty(key) || StringUtils.isEmpty(GetEnumKey) || StringUtils.isEmpty(GetEnumValue)){
            return  result;
        }
        Enum<?>[] enumConstants = enumCalss.getEnumConstants();

        try {
            Method keyMethod = enumCalss.getMethod(GetEnumKey);
            Method valueMethod = enumCalss.getMethod(GetEnumValue);

            keyMethod.setAccessible(true);
            valueMethod.setAccessible(true);
            for (Enum<?> enumConstant : enumConstants) {
                Object invoke = keyMethod.invoke(enumConstant);
                if (key.equals(invoke)) {
                    result = valueMethod.invoke(enumConstant);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 判断某个值 是否在枚举类中是否存在
     * @param key
     * @param enumCalss
     * @param GetEnumKey
     * @return
     */
    public static boolean isInclude(Object key, Class<? extends Enum<?>> enumCalss, String GetEnumKey){
        boolean result=false;
        if(ObjectUtils.isEmpty(key) || StringUtils.isEmpty(GetEnumKey)){
            return  result;
        }
        Enum<?>[] enumConstants = enumCalss.getEnumConstants();

        try {
            Method keyMethod = enumCalss.getMethod(GetEnumKey);
            keyMethod.setAccessible(true);
            for (Enum<?> enumConstant : enumConstants) {
                Object invoke = keyMethod.invoke(enumConstant);
                if (key.equals(invoke)) {
                    result=true;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
