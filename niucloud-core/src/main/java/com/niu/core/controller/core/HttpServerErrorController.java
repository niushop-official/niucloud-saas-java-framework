package com.niu.core.controller.core;

import com.niu.core.common.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;

@RestController
@Slf4j
public class HttpServerErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public Result handleError(WebRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code", WebRequest.SCOPE_REQUEST);
        if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            Result result = Result.fail(statusCode, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            result.setData(request.getContextPath());
            return result;
        } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
            Result result = Result.fail(statusCode, HttpStatus.NOT_FOUND.getReasonPhrase());
            result.setData(request.getContextPath());
            return result;
        } else {
            Result result = Result.fail(statusCode, String.valueOf(statusCode));
            result.setData(request.getContextPath());
            return result;
        }
    }

}