package com.niu.core.common.utils.reflect;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 反射获取方法
 */
@Log4j2
public class ReflectCallMethod {

    /**
     * @param entryClass
     * @param methodName
     * @param <T>
     * @return
     */
    public static <T> T invokeForStatic(Class entryClass, String methodName) {
        if (entryClass == null) {
            return null;
        }
        T result = null;
        try {
            // 反射获取
            Method method = entryClass.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(entryClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param entryClass
     * @param methodName
     * @param classArgs
     * @param valueArgs
     * @param <T>
     * @return
     */
    public static <T> T invokeForStatic(Class entryClass, String methodName, Class[] classArgs, Object[] valueArgs) {
        if (entryClass == null) {
            return null;
        }
        T result = null;
        try {
            Method method = entryClass.getDeclaredMethod(methodName, classArgs);
            method.setAccessible(true);
            return (T) method.invoke(entryClass, valueArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param entryClass
     * @param methodName
     * @param initArgs
     * @param <T>
     * @return
     */
    public static <T> T invokeForStatic(Class entryClass, String methodName, List<Object> initArgs) {
        if (entryClass == null) {
            return null;
        }
        T result = null;
        try {
            Class[] classArgs = new Class[initArgs.size()];
            Object[] valueArgs = new Object[initArgs.size()];
            for (int i = 0; i < initArgs.size(); i++) {
                classArgs[i] = initArgs.get(i).getClass();
                valueArgs[i] = initArgs.get(i);
            }
            Method method = entryClass.getDeclaredMethod(methodName, classArgs);
            method.setAccessible(true);
            return (T) method.invoke(entryClass, valueArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param entryObject
     * @param methodName
     * @param <T>
     * @return
     */
    public static <T> T invokeForInstance(Object entryObject, String methodName) {
        if (entryObject == null) {
            return null;
        }
        T result = null;
        try {
            // 反射获取
            Method method = entryObject.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (T) method.invoke(entryObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param entryObject
     * @param methodName
     * @param classArgs
     * @param valueArgs
     * @param <T>
     * @return
     */
    public static <T> T invokeForInstance(Object entryObject, String methodName, Class[] classArgs, Object[] valueArgs) {
        if (entryObject == null) {
            return null;
        }
        T result = null;
        try {
            Method method = entryObject.getClass().getDeclaredMethod(methodName, classArgs);
            method.setAccessible(true);
            return (T) method.invoke(entryObject, valueArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param entryObject
     * @param methodName
     * @param initArgs
     * @param <T>
     * @return
     */
    public static <T> T invokeForInstance(Object entryObject, String methodName, List<Object> initArgs) {
        if (entryObject == null) {
            return null;
        }
        T result = null;
        try {
            Class[] classArgs = new Class[initArgs.size()];
            Object[] valueArgs = new Object[initArgs.size()];
            for (int i = 0; i < initArgs.size(); i++) {
                classArgs[i] = initArgs.get(i).getClass();
                valueArgs[i] = initArgs.get(i);
            }
            Method method = entryObject.getClass().getDeclaredMethod(methodName, classArgs);
            method.setAccessible(true);
            return (T) method.invoke(entryObject, valueArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    //=============================================================================================//
    // getter and setter
    //=============================================================================================//

    /**
     * @param entryObject
     * @param methodName
     * @param <E>
     * @return
     */
    public static <E> E getInvoke(Object entryObject, String methodName) {
        if (entryObject == null || StringUtils.isEmpty(methodName)) {
            return null;
        }
        try {
            Method method = entryObject.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            Object returnValue = method.invoke(entryObject);
            return (E) returnValue;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param entryObject
     * @param fieldName
     * @param <E>
     * @return
     */
    public static <E> E getInvokeByFieldName(Object entryObject, String fieldName) {
        if (entryObject == null || StringUtils.isEmpty(fieldName)) {
            return null;
        }
        try {
            String methodName = "get" + StringUtils.capitalize(fieldName);
            Method method = entryObject.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            Object returnValue = method.invoke(entryObject);
            return (E) returnValue;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param entryObject
     * @param methodName
     * @param <E>
     * @return
     */
    public static <E> E setInvoke(Object entryObject, String methodName, Object object) {
        if (entryObject == null || StringUtils.isEmpty(methodName)) {
            return null;
        }
        try {
            Method method = entryObject.getClass().getDeclaredMethod(methodName, object.getClass());
            method.setAccessible(true);
            Object returnValue = method.invoke(entryObject, object);
            return (E) returnValue;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * @param entryObject
     * @param fieldName
     * @param <E>
     * @return
     */
    public static <E> E setInvokeByFieldName(Object entryObject, String fieldName, Object object) {
        if (entryObject == null || StringUtils.isEmpty(fieldName)) {
            return null;
        }
        try {
            String methodName = "set" + StringUtils.capitalize(fieldName);
            Method method = entryObject.getClass().getDeclaredMethod(methodName, object.getClass());
            method.setAccessible(true);
            Object returnValue = method.invoke(entryObject, object);
            return (E) returnValue;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void setFieldValue(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
            log.debug("成功设置字段 {} 的值为: {}", field.getName(), value);
        } catch (Exception e) {
            log.error("设置字段失败: {}", field.getName(), e);
        }
    }

    //=============================================================================================//
    // the end
    //=============================================================================================//

}
