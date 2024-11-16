package com.niu.core.common.config;

import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class NiuExceptionHandler {

    /**
     * 请求方式不支持
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
    public Result handleException(HttpRequestMethodNotSupportedException e) {
        return Result.fail("不支持' " + e.getMethod() + "'请求");
    }

    /**
     * 通过用
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("全局异常已处理: Exception-->", e);
        e.printStackTrace();
        if (e instanceof BaseException) {
            BaseException baseException = (BaseException) e;
            return Result.fail(baseException.getCode(), baseException.getMsg());
        }
        return Result.fail(e.getMessage());
    }

    /**
     * 通过用
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("全局异常已处理: RuntimeException-->", e);
        if (e instanceof BaseException) {
            BaseException baseException = (BaseException) e;
            return Result.fail(baseException.getCode(), baseException.getMsg());
        }
        return Result.fail(e.getMessage());
    }

    /**
     * 方法参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("全局异常已处理: MethodArgumentNotValidException-->", e);
        e.printStackTrace();
        log.error(e.getMessage(), e);
        return Result.fail(e.getBindingResult().getFieldError().getDefaultMessage());
    }

}
