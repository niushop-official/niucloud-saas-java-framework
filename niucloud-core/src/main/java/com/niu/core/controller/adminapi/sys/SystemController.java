package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.admin.sys.param.SpreadQrcodeParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取系统环境配置
     */
    @GetMapping("/system")
    public Result<?> getSystemInfo() {
        return Result.success(systemService.getSystemInfo());
    }

    /**
     * 推广二维码
     * @param param
     * @return
     */
    @PostMapping("/qrcode")
    public Result<?> qrcode(@RequestBody SpreadQrcodeParam param) {
        return Result.success(systemService.getSpreadQrcode(param));
    }

}
