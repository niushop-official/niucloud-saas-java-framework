package com.niu.core.common.exception;

public class CommonException extends BaseException {

    public CommonException(String msg, Object... arguments) {
        super(0, msg, arguments);
    }

    public CommonException(Integer code, String msg, Object... arguments) {
        super(code, msg, arguments);
    }
}

