package com.niu.core.common.domain;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.*;

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

    /**
     * 字符串转化为JSON对象/数组 并且将驼峰转下换线
     */
    public static class StringToJsonCamelToUnderlineSerializer extends JsonSerializer<String> {

        @Override
        public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (value == null || value.trim().isEmpty()) {
                jsonGenerator.writeNull();
                return;
            }

            // Redis序列化上下文：直接写入原始字符串（保留原逻辑）
            if (isRedisSerializationContext()) {
                jsonGenerator.writeString(value);
                return;
            }

            // 普通场景：解析JSON并转换key为驼峰
            try {
                JSON json = JSONUtil.parse(value);
                if (json instanceof JSONObject) {
                    // JSON对象：递归转换所有key为驼峰
                    Map<String, Object> camelMap = convertCamelToUnderline((JSONObject) json);
                    jsonGenerator.writeObject(camelMap);
                } else if (json instanceof JSONArray) {
                    // JSON数组：递归处理数组内的每个元素
                    List<Object> camelList = convertArrayCamelToUnderline((JSONArray) json);
                    jsonGenerator.writeObject(camelList);
                } else {
                    // 非JSON对象/数组：直接输出字符串
                    jsonGenerator.writeString(value);
                }
            } catch (Exception e) {
                // 解析失败：回退为原始字符串
                jsonGenerator.writeString(value);
            }
        }

        /**
         * 递归处理JSONObject：将所有驼峰key转为下划线（支持嵌套对象）
         * 示例：userName → user_name，orderInfo → order_info
         */
        private Map<String, Object> convertCamelToUnderline(JSONObject jsonObject) {
            Map<String, Object> resultMap = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                // 驼峰key转下划线（Hutool工具类自动处理大写开头/连续大写）
                String underlineKey = StrUtil.toUnderlineCase(key);
                Object value = jsonObject.get(key);

                // 递归处理嵌套的JSONObject
                if (value instanceof JSONObject) {
                    resultMap.put(underlineKey, convertCamelToUnderline((JSONObject) value));
                }
                // 递归处理嵌套的JSONArray
                else if (value instanceof JSONArray) {
                    resultMap.put(underlineKey, convertArrayCamelToUnderline((JSONArray) value));
                }
                // 普通值：直接放入
                else {
                    resultMap.put(underlineKey, value);
                }
            }
            return resultMap;
        }

        /**
         * 递归处理JSONArray：遍历数组元素，处理其中的JSONObject/JSONArray
         */
        private List<Object> convertArrayCamelToUnderline(JSONArray jsonArray) {
            List<Object> resultList = new ArrayList<>();
            for (Object element : jsonArray) {
                // 元素是JSONObject：转换驼峰key为下划线
                if (element instanceof JSONObject) {
                    resultList.add(convertCamelToUnderline((JSONObject) element));
                }
                // 元素是JSONArray：递归处理
                else if (element instanceof JSONArray) {
                    resultList.add(convertArrayCamelToUnderline((JSONArray) element));
                }
                // 普通元素：直接放入
                else {
                    resultList.add(element);
                }
            }
            return resultList;
        }

        /**
         * 判断是否为Redis序列化上下文（保留原逻辑，建议后续替换为ThreadLocal优化）
         */
        private boolean isRedisSerializationContext() {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                if (className.contains("Redis") || className.contains("Jackson2JsonRedisSerializer")) {
                    return true;
                }
            }
            return false;
        }
    }
}
