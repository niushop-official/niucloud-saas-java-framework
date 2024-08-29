package com.niu.core.common.domain;

import com.niu.core.common.enums.HttpEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1000L;

    private Integer code;

    private String msg;

    private T data;

    public static <T> Result<Object> success() {
        return instance(HttpEnum.SUCCESS.getCode(), HttpEnum.SUCCESS.getMsg(), new ArrayList<>());
    }

    public static <T> Result<Object> success(Integer code) {
        return instance(code, HttpEnum.SUCCESS.getMsg(), new ArrayList<>());
    }
    public static <T> Result<String> success(String msg) {
        return instance(HttpEnum.SUCCESS.getCode(), msg, "");
    }


    public static <T> Result<T> success(T data) {
        return instance(HttpEnum.SUCCESS.getCode(), HttpEnum.SUCCESS.getMsg(), data);
    }

    public static <T> Result<Object> success(Integer code, String msg) {
        return instance(code, msg, new ArrayList<>());
    }
    public static <T> Result<T> success(String msg, T data) {
        return instance(HttpEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> Result<T> success(Integer code, String msg, T data) {
        return instance(code, msg, data);
    }


    public static <T> Result<Object> fail() {
        return instance(HttpEnum.Fail.getCode(), HttpEnum.Fail.getMsg(), new ArrayList<>());
    }

    public static <T> Result<Object> fail(Integer code) {
        return instance(code, HttpEnum.Fail.getMsg(), new ArrayList<>());
    }

    public static <T> Result<Object> fail(String msg) {
        return instance(HttpEnum.Fail.getCode(), msg, new ArrayList<>());
    }

    public static <T> Result<T> fail(T data) {
        return instance(HttpEnum.Fail.getCode(), HttpEnum.Fail.getMsg(), data);
    }

    public static <T> Result<Object> fail(Integer code, String msg) {
        return instance(code, msg, new ArrayList<>());
    }

    public static <T> Result<T> fail(String msg, T data) {
        return instance(HttpEnum.Fail.getCode(), msg, data);
    }

    public static <T> Result<T> fail(Integer code, String msg, T data) {
        return instance(code, msg, data);
    }

    public static <T> Result<T> instance(Integer code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        if (msg == null) {
            result.setMsg("");
        } else {
            result.setMsg(msg);
        }
        result.setData(data);
        return result;
    }

    /*
     * toString()
     */
    @Override
    public String toString() {
        return "{"
                + "\"code\": " + this.getCode()
                + ", \"msg\": \"" + this.getMsg() + "\""
                + ", \"data\": \"" + this.getData() + "\""
                + "}";
    }

}
