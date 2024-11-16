package com.niu.core.common.utils.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.utils.date.DateUtils;

import java.math.BigDecimal;

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
}
