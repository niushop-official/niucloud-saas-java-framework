package com.niu.core.common.exception;

public class ApiException extends BaseException {

    public ApiException(String msg, Object... arguments) {
        super(0, msg, arguments);
    }

    public ApiException(Integer code, String msg, Object... arguments) {
        super(code, msg, arguments);

    }

}

