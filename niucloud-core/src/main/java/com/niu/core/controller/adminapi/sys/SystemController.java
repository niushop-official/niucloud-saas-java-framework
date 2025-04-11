package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISystemService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统控制器
 */

@RestController
@RequestMapping("adminapi/sys")
@SaCheckLogin
public class SystemController {

    @Resource
    ISystemService systemService;

    /**
     * 清理缓存
     * @return
     */
    @PostMapping("/cache/clear")
    public Result<?> clearCache() {
        systemService.clearCache();
        return Result.success();
    }



}
