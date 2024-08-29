package com.niu.core.common.loader;

public class SystemLoader {


    /**
     * 通过驱动装载一个类
     *
     * @param dirver
     * @param <T>
     * @return
     */
    public static <T> T forName(String dirver) {
        T target = null;
        try {
            Class<?> clazz = Class.forName(dirver);
            target = (T) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;
    }

}
