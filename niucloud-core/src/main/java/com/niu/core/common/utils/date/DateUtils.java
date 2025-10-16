package com.niu.core.common.utils.date;


import cn.hutool.core.date.DateUtil;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 针对hutool二次封装时间方便调用
 */
public class DateUtils {

    private static final List<String> datePatterns = List.of(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy年MM月dd日 HH时mm分ss秒",
            "yyyy年MM月dd日",
            "MM/dd/yyyy HH:mm:ss",
            "MM-dd-yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss"
    );

    /**
     * 时间戳转化成时间格式
     *
     * @param timestamp
     * @return
     */
    public static String timestampToString(long timestamp) {
        //转化成毫秒
        if(timestamp == 0L)
        {
            return "";
        }
        long minTimestamp = timestamp * 1000;
        Date date = DateUtil.date(minTimestamp);
        return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String timestampToString(long timestamp, String format) {
        //转化成毫秒
        if(timestamp == 0L)
        {
            return "";
        }
        long minTimestamp = timestamp * 1000;
        Date date = DateUtil.date(minTimestamp);
        return DateUtil.format(date, format);
    }

    /**
     * 时间字符串转化成时间戳
     *
     * @param date
     * @return
     */
    public static Long StringToTimestamp(String date) {

        if (date.equals("") || date.equals("0")) {
            return 0L;
        }
        java.util.Date dateTime = DateUtil.parse(date);
        // 获取时间戳
        return dateTime.getTime() / 1000;
    }

    /**
     * 获取当前时间戳
     *
     * @return long
     */
    public static long currTime() {

        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String currDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取凌晨时间戳
     *
     * @return
     */
    public static long currInitTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(date.getTime());
        return StringToTimestamp(today);
    }

    /**
     * 获取凌晨时间
     *
     * @return
     */
    public static String currInitDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(date.getTime());
        return today;
    }

    /**
     * 当前时间加几天
     *
     * @param dayNum
     * @return
     */
    public static String getDateAddDay(Integer dayNum) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, dayNum);
        Date resultDate = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(resultDate);
    }

    /**
     * 当前时间加几个月
     *
     * @param month
     * @return
     */
    public static String getDateAddMonth(Integer month) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        Date resultDate = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(resultDate);
    }


    /**
     * 当天时间的开始结束时间
     *
     * @param currDate
     * @return
     */
    public static String[] getStartEndByDay(String currDate) {
        String[] dates = new String[2];
        Date startDate = DateUtil.parse(currDate, "yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        dates[0] = DateUtil.format(startDate, "yyyy-MM-dd");
        dates[1] = DateUtil.format(endDate, "yyyy-MM-dd HH:mm:ss");
        return dates;
    }

    /**
     * 获取当天的格式化时间
     *
     * @return
     */
    public static String getFormatTodayString() {
        return DateUtil.format(new Date(), "yyyy-MM-dd");
    }

    /**
     * 获取当天的开始时间（00:00:00）
     * @return
     */
    public static long getStartOfDayTimestamp() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().getEpochSecond();
    }

    /**
     * 获取当天的结束时间（23:59:59）
     * @return
     */
    public static long getEndOfDayTimestamp() {
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        ZonedDateTime zonedDateTime = endOfDay.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().getEpochSecond();
    }

    /**
     * 获取昨天的格式化时间
     *
     * @return
     */
    public static String getFormatYesterdayString() {
        long timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        return DateUtil.format(new Date(timestamp), "yyyy-MM-dd");
    }

    /**
     * 格式化时间戳
     */
    public static String formatTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH时mm分");
        return dateTime.format(formatter);
    }

    /**
     * 解析时间字符串
     */
    public static long parseTimeString(String timeStr) {
        try {
            // 先尝试作为时间戳解析
            return Long.parseLong(timeStr);
        } catch (NumberFormatException e) {
            // 作为日期字符串解析
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(timeStr, formatter);
            return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        }
    }

    /**
     * 将时间戳转为无时分秒的字符串
     * @return
     */
    public static String timestampToNoHMS(long timestamp) {
        return DateUtil.format(new Date(timestamp * 1000), "yyyy-MM-dd");
    }


    /**
     * 将不同类型的时间字符串转换为时间戳（秒级）
     */
    public static Long strToTimestamp(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return 0L;
        }

        String cleanedStr = timeStr.trim();

        // 优先尝试解析ISO 8601格式（包含时区信息）
        try {
            Instant instant = Instant.parse(cleanedStr);
            return instant.getEpochSecond();
        } catch (DateTimeParseException e) {
            // 继续尝试其他格式
        }

        // 尝试解析数字时间戳（放在格式解析之前更合理）
        try {
            long timestamp = Long.parseLong(cleanedStr);
            if (timestamp > 1000000000000L) { // 毫秒级
                return timestamp / 1000;
            }
            return timestamp;
        } catch (NumberFormatException e) {
            // 继续尝试日期格式
        }


        // 解析其他日期格式
        for (String pattern : datePatterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

                if (pattern.contains("HH:mm:ss") || pattern.contains("HH时mm分ss秒")) {
                    // 对于没有时区的时间，明确指定时区策略
                    LocalDateTime dateTime = LocalDateTime.parse(cleanedStr, formatter);
                    // 使用系统时区，但建议明确文档说明
                    return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
                } else {
                    // 只有日期的，使用当天的开始时间
                    LocalDate date = LocalDate.parse(cleanedStr, formatter);
                    return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
                }
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        return 0L;
    }

}
