package com.niu.core.controller.api.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.GetPosterParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 海报控制器
 */

@RestController
@RequestMapping("api/poster")
public class SysPosterController {

    @Resource
    private ICorePosterService corePosterService;

    @GetMapping("")
    public Result<?> poster(GetPosterParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setChannel(RequestUtils.channel());
        return Result.success("", corePosterService.get(param));
    }

}
