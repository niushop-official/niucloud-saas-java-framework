package com.niu.core.common.utils.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.utils.date.DateUtils;

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

}
