package com.niu.core.common.component.upload.exception;

import com.niu.core.common.exception.BaseException;

public class UploadException extends BaseException {

    public UploadException(String msg, Object... arguments) {
        super(msg, arguments);
    }

    public UploadException(Integer code, String msg, Object... arguments) {
        super(code, msg, arguments);
    }
}

