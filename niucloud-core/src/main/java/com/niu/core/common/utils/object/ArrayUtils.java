package com.niu.core.common.utils.object;

import cn.hutool.core.util.ObjectUtil;

import java.util.List;

/**
 * 数组处理工具类
 */
public class ArrayUtils {

    /**
     * 两个数组合并
     * @param objects
     * @param objectArray
     * @return
     */
    public static Object[] arrayMerge(Object[] objects, Object[] objectArray){
        if(ObjectUtil.isNull(objects)){
            objects=new Object[0];
        }
        if(ObjectUtil.isNull(objectArray)){
            objectArray=new Object[0];
        }
        Object[] mergedArray =new Object[objects.length+objectArray.length];
        System.arraycopy(objects, 0, mergedArray ,0, objects.length);
        System.arraycopy(objectArray, 0, mergedArray, objects.length, objectArray.length);
        return mergedArray;
    }
}
