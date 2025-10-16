package com.niu.core.common.utils.mapper;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.utils.date.DateUtils;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
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

    public static QueryWrapper buildByTime(QueryWrapper queryWrapper, String fieldName, List<String> stringTimes) {

        Long startTime = (stringTimes.get(0) == null) ? 0 : DateUtils.StringToTimestamp(stringTimes.get(0));
        Long endTime = (stringTimes.size() == 1) ? 0 : DateUtils.StringToTimestamp(stringTimes.get(1));
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

    public static <T> LambdaQueryWrapper<T> buildByTime(LambdaQueryWrapper queryWrapper, SFunction<T, ?> column, List<String> stringTimes) {

        Long startTime = (stringTimes.get(0) == null) ? 0 : DateUtils.StringToTimestamp(stringTimes.get(0));
        Long endTime = (stringTimes.size() == 1) ? 0 : DateUtils.StringToTimestamp(stringTimes.get(1));
        if(startTime == 0 && endTime == 0){
            return queryWrapper;
        }
        if (startTime > 0 && endTime > 0) {
            queryWrapper.between(column, startTime, endTime);
        } else if (startTime > 0 && endTime == 0) {
            queryWrapper.ge(column, startTime);
        } else if (startTime == 0 && endTime > 0) {
            queryWrapper.le(column, startTime);
        }
        return queryWrapper;
    }


    public static <T> LambdaQueryWrapper<T> buildByTime(LambdaQueryWrapper<T> queryWrapper, SFunction<T, ?> column, String[] stringTimes) {
        Long startTime = (stringTimes[0] == null) ? 0 : DateUtils.StringToTimestamp(stringTimes[0]);
        Long endTime = (stringTimes.length == 1) ? 0 : DateUtils.StringToTimestamp(stringTimes[1]);

        if (startTime == 0 && endTime == 0) {
            return queryWrapper;
        }

        if (startTime > 0 && endTime > 0) {
            queryWrapper.between(column, startTime, endTime);
        } else if (startTime > 0 && endTime == 0) {
            queryWrapper.ge(column, startTime);
        } else if (startTime == 0 && endTime > 0) {
            queryWrapper.le(column, endTime);
        }
        return queryWrapper;
    }

    public static <T> MPJLambdaWrapper<T> buildByTime(MPJLambdaWrapper<T> queryWrapper, SFunction<T, ?> column, String[] stringTimes) {
        Long startTime = (stringTimes[0] == null) ? 0 : DateUtils.StringToTimestamp(stringTimes[0]);
        Long endTime = (stringTimes.length == 1) ? 0 : DateUtils.StringToTimestamp(stringTimes[1]);

        if (startTime == 0 && endTime == 0) {
            return queryWrapper;
        }

        if (startTime > 0 && endTime > 0) {
            queryWrapper.between(column, startTime, endTime);
        } else if (startTime > 0 && endTime == 0) {
            queryWrapper.ge(column, startTime);
        } else if (startTime == 0 && endTime > 0) {
            queryWrapper.le(column, endTime);
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
