package com.niu.core.common.utils.mapper;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.utils.date.DateUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.function.Function;

public class QueryMapperUtils {

    /**
     * @param queryWrapper
     * @param fieldName    字段名称
     * @param stringTimes  变量
     * @return
     */
    public static QueryWrapper buildByTime(QueryWrapper queryWrapper, String fieldName, String[] stringTimes) {

        Long startTime = (stringTimes[0] == null) ? 0 : DateUtils.StringToTimestamp(stringTimes[0]);
        Long endTime = (stringTimes.length == 1) ? 0 : DateUtils.StringToTimestamp(stringTimes[1]);
        if(startTime == 0 && endTime == 0){
            return queryWrapper;
        }
        if (startTime > 0 && endTime > 0) {
            queryWrapper.between(fieldName, startTime, endTime);
        } else if (startTime > 0 && endTime == 0) {
            queryWrapper.ge(fieldName, startTime);
        } else if (startTime == 0 && endTime > 0) {
            queryWrapper.le(fieldName, startTime);
        }
        return queryWrapper;
    }

    public static LambdaQueryWrapper buildByTime(LambdaQueryWrapper queryWrapper, String fieldName, String[] stringTimes) {
        Long startTime = (stringTimes[0] == null) ? 0 : DateUtils.StringToTimestamp(stringTimes[0]);
        Long endTime = (stringTimes.length == 1) ? 0 : DateUtils.StringToTimestamp(stringTimes[1]);
        if (startTime == 0 && endTime == 0) {
            return queryWrapper;
        }
        if (startTime > 0 && endTime > 0) {
            queryWrapper.between(getField(fieldName), startTime, endTime);
        } else if (startTime > 0 && endTime == 0) {
            queryWrapper.ge(getField(fieldName), startTime);
        } else if (startTime == 0 && endTime > 0) {
            queryWrapper.le(getField(fieldName), endTime);
        }
        return queryWrapper;
    }

    private static <T> Function<T, Object> getField(String fieldName) {
        return t -> {
            try {
                Method method = t.getClass().getMethod("get" + capitalize(fieldName));
                return method.invoke(t);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
            }
        };
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * @param queryWrapper
     * @param fieldName    字段名称
     * @param stringTimes  变量
     * @return
     */
    public static MPJQueryWrapper buildByTime(MPJQueryWrapper queryWrapper, String fieldName, String[] stringTimes) {
        Long startTime = (stringTimes[0] == null) ? 0 : DateUtils.StringToTimestamp(stringTimes[0]);
        Long endTime = (stringTimes.length == 1) ? 0 : DateUtils.StringToTimestamp(stringTimes[1]);
        if (startTime > 0 && endTime > 0) {
            queryWrapper.between(fieldName, startTime, endTime);
        } else if (startTime > 0 && endTime == 0) {
            queryWrapper.ge(fieldName, startTime);
        } else if (startTime == 0 && endTime > 0) {
            queryWrapper.le(fieldName, startTime);
        }
        return queryWrapper;
    }

    /**
     *
     * @param queryWrapper
     * @param fieldName
     * @param arr
     * @return
     */
    public static <N extends Number> QueryWrapper buildByBetween(QueryWrapper queryWrapper, String fieldName, N[] arr) {
        BigDecimal zero = new BigDecimal(0);
        BigDecimal start = (arr[0] == null) ? zero : new BigDecimal(String.valueOf(arr[0]));
        BigDecimal end = (arr.length == 1) ? zero : new BigDecimal(String.valueOf(arr[1]));

        if (start.compareTo(zero) > 0 && end.compareTo(zero) > 0) {
            queryWrapper.between(fieldName, start, end);
        } else if (start.compareTo(zero) > 0 && end.compareTo(zero) == 0) {
            queryWrapper.ge(fieldName, start);
        } else if (start.compareTo(zero) == 0 && end.compareTo(zero) > 0) {
            queryWrapper.le(fieldName, start);
        }
        return queryWrapper;
    }

    /**
     *
     * @param queryWrapper
     * @param fieldName
     * @param arr
     * @return
     */
    public static <N extends Number> MPJQueryWrapper buildByBetween(MPJQueryWrapper queryWrapper, String fieldName, N[] arr) {
        BigDecimal zero = new BigDecimal(0);
        BigDecimal start = (arr[0] == null) ? zero : new BigDecimal(String.valueOf(arr[0]));
        BigDecimal end = (arr.length == 1) ? zero : new BigDecimal(String.valueOf(arr[1]));

        if (start.compareTo(zero) > 0 && end.compareTo(zero) > 0) {
            queryWrapper.between(fieldName, start, end);
        } else if (start.compareTo(zero) > 0 && end.compareTo(zero) == 0) {
            queryWrapper.ge(fieldName, start);
        } else if (start.compareTo(zero) == 0 && end.compareTo(zero) > 0) {
            queryWrapper.le(fieldName, start);
        }
        return queryWrapper;
    }


    /**
     *  多字典模糊查询
     * @param wrapper
     * @param keyword
     * @param columns
     * @param <T>
     */
    public static <T> void addMultiLike(MPJQueryWrapper<T> wrapper, String keyword, String... columns) {
        if (StrUtil.isBlank(keyword) || ArrayUtil.isEmpty(columns)) {
            return;
        }
        wrapper.and(inner -> {
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    inner.or();
                }
                inner.like(columns[i], keyword);
            }
        });
    }
}
