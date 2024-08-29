package com.niu.core.common.utils.object;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.utils.reflect.ReflectCallMethod;

import java.util.*;

public class ObjectGroupUtils {

    /**
     * @param dataList
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, T> group(List<T> dataList, String fieldName) {
        if (dataList != null && StrUtil.isEmpty(fieldName)) {
            return null;
        }
        Map<K, T> dataMap = new HashMap<>();
        for (T target : dataList) {
            K k = ReflectCallMethod.getInvokeByFieldName(target, fieldName);
            if (dataMap.get(k) == null) {
                dataMap.put(k, target);
            }
        }
        return dataMap;
    }

    /**
     * @param dataSet
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, T> group(Set<T> dataSet, String fieldName) {
        return group(new ArrayList<>(dataSet), fieldName);
    }


    /**
     * @param dataList
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, List<T>> groupList(List<T> dataList, String fieldName) {
        if (dataList != null && StrUtil.isEmpty(fieldName)) {
            return null;
        }
        Map<K, List<T>> dataMap = new HashMap<>();
        for (T target : dataList) {
            K k = ReflectCallMethod.getInvokeByFieldName(target, fieldName);
            if (dataMap.get(k) == null) {
                List<T> valueList = new ArrayList<>();
                valueList.add(target);
                dataMap.put(k, valueList);
            } else {
                List<T> valueList = dataMap.get(k);
                valueList.add(target);
                dataMap.put(k, valueList);
            }
        }
        return dataMap;
    }


    /**
     * @param dataSet
     * @param fieldName
     * @param <T>
     * @return
     */
    public static <K, T> Map<K, List<T>> groupList(Set<T> dataSet, String fieldName) {
        return groupList(new ArrayList<>(dataSet), fieldName);
    }

}
