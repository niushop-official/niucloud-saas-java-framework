package com.niu.core.common.domain;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonViewUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JSON序列化
 */
public final class BeanJsonSerializer {

    /**
     * 配置:   @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
     */
    public static class LongDateToStringSerializer extends JsonSerializer<Long> {

        @Override
        public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // 如果是Redis序列化上下文，直接写入字符串
            if (isRedisSerializationContext()) {
                jsonGenerator.writeNumber(value);
                return;
            }

            // 按照自定义格式转换
            String formatStr = "";
            if (value != null && value > 0) {
                formatStr = DateUtils.timestampToString(value);
            }
            jsonGenerator.writeString(formatStr);
        }

        private boolean isRedisSerializationContext() {
            // 通过堆栈信息判断是否在Redis序列化过程中
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                if (element.getClassName().contains("Redis") ||
                        element.getClassName().contains("Jackson2JsonRedisSerializer")) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 配置:   @JsonSerialize(using = BeanJsonSerializer.DateToStringSerializer.class)
     */
    public static class DateToStringSerializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            String formatStr = "";
            if (date != null) {
                formatStr = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
            }
            jsonGenerator.writeString(formatStr);
        }

    }

    public static class LocalDateTimeToStringSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            String formatStr = "";
            if (localDateTime != null) {
                formatStr = LocalDateTimeUtil.format(localDateTime, "yyyy-MM-dd HH:mm:ss");
            }
            jsonGenerator.writeString(formatStr);
        }

    }

    /**
     * 配置:   @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
     */
    public static class StringToJsonSerializer extends JsonSerializer<String> {

        @Override
        public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (value == null || value.trim().isEmpty()) {
                jsonGenerator.writeNull();
                return;
            }

            // 如果是Redis序列化上下文，直接写入字符串
            if (isRedisSerializationContext()) {
                jsonGenerator.writeString(value);
                return;
            }

            try {
                JSON json = JSONUtil.parse(value);

                if (json instanceof JSONObject) {
                    Map<String, Object> map = JSONUtil.toBean(value, Map.class);
                    jsonGenerator.writeObject(map);
                } else if (json instanceof JSONArray) {
                    List<Object> list = JSONUtil.toList(value, Object.class);
                    jsonGenerator.writeObject(list);
                } else {
                    jsonGenerator.writeString(value);
                }
            } catch (Exception e) {
                jsonGenerator.writeString(value);
            }
        }

        private boolean isRedisSerializationContext() {
            // 通过堆栈信息判断是否在Redis序列化过程中
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                if (element.getClassName().contains("Redis") ||
                        element.getClassName().contains("Jackson2JsonRedisSerializer")) {
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * BigDecimal，显示为带2个小数
     */
    public static class BigDecimalFormatSerializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            try {
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                jsonGenerator.writeObject(decimalFormat.format(value));
            } catch (Exception e) {
                // ignore
            }
        }

    }

    /**
     * BigDecimal，显示为整型
     */
    public static class BigDecimalToIntegerSerializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            try {
                DecimalFormat decimalFormat = new DecimalFormat("#0");
                jsonGenerator.writeObject(decimalFormat.format(value));
            } catch (Exception e) {
                // ignore
            }
        }
    }


    /**
     * 字符串转化为JSON对象
     *
     * 配置:   @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
     */
    public static class StringToJSONObjectSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            if(ObjectUtil.isNotNull(s) && ObjectUtil.isNotEmpty(s)){
                try {
                    JSONObject jsonObject=JSONUtil.parseObj(s);
                    jsonGenerator.writeObject(jsonObject);
                }catch (Exception e){
                    try{
                        JSONArray jsonArray=JSONUtil.parseArray(s);
                        jsonGenerator.writeObject(jsonArray);
                    }catch (Exception ex){
                        jsonGenerator.writeString(s);
                    }
                }
            }else{
                jsonGenerator.writeObject(new JSONObject());
            }
        }
    }

}
