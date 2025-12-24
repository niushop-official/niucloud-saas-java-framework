package com.niu.core.controller.api.niucloud;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.service.core.niucloud.ICoreNotifyService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Niucloud {

    @Resource
    ICoreNotifyService coreNotifyService;

    @SaIgnore
    @PostMapping("/niucloud/notify")
    public Result notify(HttpServletRequest request, HttpServletResponse response) {
        return coreNotifyService.notify(request, response);
    }
}
