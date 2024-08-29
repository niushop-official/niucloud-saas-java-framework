package com.niu.core.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.BeanUtils;

import java.util.*;

public class CollectUtils {

    /**
     * 根据类模板构建对象，同时复制全部属性
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T build(Object source, Class<T> targetClass) {
        if (source == null || targetClass == null) {
            return null;
        }
        try {
            T target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    /**
     * 同时复制全部属性
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T buildWithProps(Object source, T target) {
        if (source == null || target == null) {
            return null;
        }
        try {
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    /**
     * 转换成所需要的对象列表
     *
     * @param sourceList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> convert(List<?> sourceList, Class<T> clazz) {
        if (sourceList == null) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>();
        for (Object source : sourceList) {
            try {
                Object target = clazz.newInstance();
                BeanUtils.copyProperties(source, target);
                targetList.add((T) target);
            } catch (Exception e) {
                // igrone
            }
        }
        return targetList;
    }

    /**
     * 完全转换
     *
     * @param sourceList
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> Set<T> convertToSet(List<?> sourceList, Class<T> targetClass) {
        Set<T> dataSet = null;
        if (!CollectionUtil.isEmpty(sourceList)) {
            dataSet = new HashSet<>();
            for (Object source : sourceList) {
                try {
                    T target = targetClass.newInstance();
                    BeanUtils.copyProperties(source, target);
                    dataSet.add(target);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return dataSet;
    }

}
