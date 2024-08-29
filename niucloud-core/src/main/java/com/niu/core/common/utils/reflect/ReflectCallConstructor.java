package com.niu.core.common.utils.reflect;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * 反射获取构造方法
 */
public class ReflectCallConstructor {

    /**
     * @param className
     * @param <T>
     * @return
     */
    public static <T> T getClass(String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) clazz;
    }

    /**
     * @param className
     * @param <T>
     * @return
     */
    public static <T> T newInstance(String className) {
        Class clazz = getClass(className);
        if (clazz != null) {
            try {
                return (T) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param className
     * @param <T>
     * @return
     */
    public static <T> T newInstanceForConstructor(String className) {
        Class clazz = getClass(className);
        if (clazz != null) {
            try {
                Constructor constructor = clazz.getConstructor();
                return (T) constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param className
     * @param <T>
     * @return
     */
    public static <T> T newInstanceForConstructor(String className, List<Object> initArgs) {
        Class clazz = getClass(className);
        if (clazz != null && initArgs != null && initArgs.size() > 0) {
            try {
                Class[] classArgs = new Class[initArgs.size()];
                Object[] valueArgs = new Object[initArgs.size()];
                for (int i = 0; i < initArgs.size(); i++) {
                    classArgs[i] = initArgs.get(i).getClass();
                    valueArgs[i] = initArgs.get(i);
                }
                Constructor constructor = clazz.getConstructor(classArgs);
                if (constructor == null) {
                    return null;
                }
                return (T) constructor.newInstance(valueArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}

