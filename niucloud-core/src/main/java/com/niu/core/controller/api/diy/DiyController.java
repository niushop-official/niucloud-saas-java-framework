package com.niu.core.controller.api.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.diy.param.DiyShareParam;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.diy.param.DiyTabbarParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/diy")
@Slf4j
public class DiyController {

    @Resource
    private IDiyService diyService;

    /**
     * 自定义页面信息
     *
     * @param param
     * @return
     */
    @GetMapping("/diy")
    public Result<?> info(DiyInfoParam param) {
        return Result.success(diyService.info(param));
    }

    @GetMapping("/tabbar")
    public Result<?> tabbar(DiyTabbarParam param) {
        return Result.success(diyService.tabbar(param));
    }

    @GetMapping("/tabbar/list")
    public Result<?> tabbarList(DiyTabbarListParam param) {
        return Result.success(diyService.tabbarList(param));
    }

    @GetMapping("/share")
    public Result<?> share(DiyShareParam param) {
        return Result.success(diyService.share(param));
    }

}
