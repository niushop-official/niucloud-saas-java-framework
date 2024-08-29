package com.niu.core.controller.adminapi.addon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 在线升级
 */
@RestController
@RequestMapping("/adminapi/upgrade")
@SaCheckLogin
public class UpgradeController {

    /**
     * 获取正在进行的升级任务
     */
    @GetMapping("/task")
    public Result<?> getUpgradeTask() {
        Object result = null;
        return Result.success(result);
    }

    /**
     * 执行升级
     */
    @PostMapping("/execute")
    public Result execute() {
        return Result.success();
    }

    /**
     * 清除升级任务
     */
    @PostMapping("/clear")
    public Result clearUpgradeTask() {
        return Result.success();
    }
}
