package com.niu.core.service.api.weapp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IServeService {

    void service(HttpServletRequest request, HttpServletResponse response);
}
