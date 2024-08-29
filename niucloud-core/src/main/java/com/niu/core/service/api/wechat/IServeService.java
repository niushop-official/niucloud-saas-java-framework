package com.niu.core.service.api.wechat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IServeService {

    void service(HttpServletRequest request, HttpServletResponse response);
}
