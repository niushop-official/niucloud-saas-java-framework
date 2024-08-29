package com.niu.core.common.utils.reflect;

import java.lang.reflect.Field;

/**
 * 反射获取成员变量
 */
public class ReflectCallField {

    /**
     * @param entryClass
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <T> T getForStatic(Class entryClass, String fieldName) {
        if (entryClass == null) {
            return null;
        }
        T result = null;
        try {
            // 反射获取
            Field callField = entryClass.getDeclaredField(fieldName);
            callField.setAccessible(true);
            return (T) callField.get(entryClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param entryClass
     * @param fieldName
     * @param value
     */
    public static void setForStatic(Class entryClass, String fieldName, Object value) {
        if (entryClass == null) {
            return;
        }
        try {
            Field callField = entryClass.getDeclaredField(fieldName);
            callField.setAccessible(true);
            callField.set(fieldName, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param entryObject
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <T> T getForInstance(Object entryObject, String fieldName) {
        if (entryObject == null) {
            return null;
        }
        T result = null;
        try {
            // 反射获取
            Field callField = entryObject.getClass().getDeclaredField(fieldName);
            callField.setAccessible(true);
            return (T) callField.get(entryObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param entryObject
     * @param fieldName
     * @param value
     */
    public static void setForInstance(Object entryObject, String fieldName, Object value) {
        if (entryObject == null) {
            return;
        }
        try {
            Field callField = entryObject.getClass().getDeclaredField(fieldName);
            callField.setAccessible(true);
            callField.set(entryObject, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
