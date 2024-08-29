package com.niu.core.common.utils.object;

import com.niu.core.common.utils.reflect.ReflectCallMethod;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CollectBuildUtils {

    /**
     * @param dataList
     * @param fieldName
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> List<K> getKeyValueList(List<T> dataList, String fieldName) {
        if (dataList == null) {
            return null;
        }
        List<K> keyList = new ArrayList<>();
        for (T target : dataList) {
            K keyValue = ReflectCallMethod.getInvokeByFieldName(target, fieldName);
            if (keyValue != null) {
                keyList.add(keyValue);
            }
        }
        return keyList;
    }

}
