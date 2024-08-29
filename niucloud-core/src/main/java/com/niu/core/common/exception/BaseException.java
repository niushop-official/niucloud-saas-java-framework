package com.niu.core.common.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 基础异常类
 */
@Data
public class BaseException extends RuntimeException {

    private Integer code;

    private String msg;

    public BaseException() {
        super("服务器异常");
        this.code = 0;
        this.msg = "服务器异常";
    }

    public BaseException(String msg, Object... arguments) {
        super(StrUtil.format(msg, arguments));
        this.code = 500;
        this.msg = StrUtil.format(msg, arguments);
    }

    public BaseException(Integer code, String msg, Object... arguments) {
        super(StrUtil.format(msg, arguments));
        this.code = code;
        this.msg = StrUtil.format(msg, arguments);
    }
}
