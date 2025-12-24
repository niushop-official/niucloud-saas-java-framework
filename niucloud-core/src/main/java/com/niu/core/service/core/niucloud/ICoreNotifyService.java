package com.niu.core.service.core.niucloud;

import com.niu.core.common.domain.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ICoreNotifyService {

    Result notify(HttpServletRequest request, HttpServletResponse response);
}
