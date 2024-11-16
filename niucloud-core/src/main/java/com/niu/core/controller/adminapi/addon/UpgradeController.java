package com.niu.core.controller.adminapi.addon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.upgrade.IUpgradeService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    IUpgradeService upgradeService;

    /**
     * 获取升级内容
     */
    @GetMapping("")
    public Result<?> getUpgradeContent() {
        return Result.success(upgradeService.getUpgradeContent(""));
    }

    /**
     * 获取升级内容
     */
    @GetMapping("/{addon}")
    public Result<?> getUpgradeContent(@PathVariable(value = "addon") String addon) {
        return Result.success(upgradeService.getUpgradeContent(addon));
    }

    /**
     * 升级
     */
    @PostMapping("")
    public Result<?> upgrade() {
        return Result.success(upgradeService.upgrade(""));
    }

    /**
     * 升级
     */
    @PostMapping("/{addon}")
    public Result<?> upgrade(@PathVariable(value = "addon") String addon) {
        return Result.success(upgradeService.upgrade(addon));
    }

    /**
     * 获取正在进行的升级任务
     */
    @GetMapping("/task")
    public Result<?> getUpgradeTask() {
        return Result.success(upgradeService.getUpgradeTask());
    }

    /**
     * 执行升级
     */
    @PostMapping("/execute")
    public Result execute() {
        upgradeService.execute();
        return Result.success();
    }

    /**
     * 清除升级任务
     */
    @PostMapping("/clear")
    public Result clearUpgradeTask() {
        upgradeService.clearUpgradeTask(0);
        return Result.success();
    }
}
