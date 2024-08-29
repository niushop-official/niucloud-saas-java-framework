package com.niu.core.common.exception;

public class AuthException extends BaseException {

    public AuthException(String msg, Integer code){
        super(code, msg);
    }
    public AuthException(String msg){
        super(msg);
    }
}

