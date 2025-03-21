package com.niu.core.controller.api.wechat;

import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.wechat.IServeService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/wechat")
public class ServeController {

    @Resource
    IServeService serveService;

    /**
     * 公众号消息推送
     * @param siteId
     */
    @SaIgnore
    @RequestMapping("/serve/{site_id}")
    public void serve(@PathVariable("site_id") Integer siteId, HttpServletRequest request, HttpServletResponse response) {
        RequestUtils.setSiteId(siteId);
        serveService.service(request, response);
    }
}
