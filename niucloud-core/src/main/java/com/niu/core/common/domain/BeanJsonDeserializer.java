package com.niu.core.common.domain;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSON反序列化
 */
public class BeanJsonDeserializer {

    /**
     * 配置:   @JsonDeserialize(using = BeanJsonDeserializer.StringDateToLongSerializer.class)
     */
    public static class StringDateToLongSerializer extends JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
            String dateString = jsonParser.getText();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                return dateFormat.parse(dateString).getTime();
            } catch (Exception e) {
                // ignore
            }
            return null;
        }

    }

    /**
     * 配置:   @JsonDeserialize(using = BeanJsonDeserializer.StringLocalDateTimeToLongSerializer.class)
     */
    public static class StringLocalDateTimeToLongSerializer extends JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
            String dateString = jsonParser.getText();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try {
                LocalDateTime tocalDateTime = LocalDateTime.parse(dateString, dateTimeFormatter);
                return Timestamp.valueOf(tocalDateTime).getTime();
            } catch (Exception e) {
                // ignore
            }
            return null;
        }

    }

}
