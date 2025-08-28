package com.niu.core.controller.adminapi.addon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.home.param.SiteSearchParam;
import com.niu.core.service.admin.home.vo.SiteListVo;
import com.niu.core.service.admin.sys.ISysUpgradeRecordsService;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsDelParam;
import com.niu.core.service.admin.sys.param.SysUpgradeRecordsSearchParam;
import com.niu.core.service.admin.sys.vo.SysUpgradeRecordsListVo;
import com.niu.core.service.admin.upgrade.IUpgradeService;
import com.niu.core.service.admin.upgrade.param.UpgradeParam;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import jdk.jfr.Description;

/**
 * 在线升级
 */
@Description("在线升级")
@RestController
@RequestMapping("/adminapi/upgrade")
@SaCheckLogin
public class UpgradeController {

    @Resource
    IUpgradeService upgradeService;

    @Resource
    ISysUpgradeRecordsService sysUpgradeRecordsService;

    /**
     * 升级记录列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Description("升级记录列表")
    @GetMapping("/records")
    public Result<PageResult<SysUpgradeRecordsListVo>> list(@Validated PageParam pageParam,
                                                            @Validated SysUpgradeRecordsSearchParam searchParam) {
        return Result.success(sysUpgradeRecordsService.page(pageParam,searchParam));
    }

    @Description("删除升级记录")
    @DeleteMapping("/records")
    public Result delete(@Validated SysUpgradeRecordsDelParam delParam) {
        sysUpgradeRecordsService.del(delParam);
        return Result.success();
    }

    /**
     * 获取升级内容
     */
    @Description("获取升级内容")
    @GetMapping("")
    public Result<?> getUpgradeContent() {
        return Result.success(upgradeService.getUpgradeContent(""));
    }

    /**
     * 获取升级内容
     */
    @Description("获取升级内容")
    @GetMapping("/{addon}")
    public Result<?> getUpgradeContent(@PathVariable(value = "addon") String addon) {
        return Result.success(upgradeService.getUpgradeContent(addon));
    }

    /**
     * 升级检测
     */
    @Description("升级检测")
    @GetMapping("/check")
    public Result<?> upgradeCheck() {
        return Result.success(upgradeService.upgradeCheck(""));
    }

    /**
     * 升级检测
     */
    @Description("升级检测")
    @GetMapping("/check/{addon}")
    public Result<?> upgradeCheck(@PathVariable(value = "addon") String addon) {
        return Result.success(upgradeService.upgradeCheck(addon));
    }

    /**
     * 升级
     */
    @Description("升级")
    @PostMapping("")
    public Result<?> upgrade(@RequestBody UpgradeParam upgradeParam) {
        return Result.success(upgradeService.upgrade(upgradeParam));
    }

    /**
     * 升级
     */
    @Description("升级")
    @PostMapping("/{addon}")
    public Result<?> upgrade(@PathVariable(value = "addon") String addon, @RequestBody UpgradeParam upgradeParam) {
        upgradeParam.setAddon(addon);
        return Result.success(upgradeService.upgrade(upgradeParam));
    }

    /**
     * 获取正在进行的升级任务
     */
    @Description("获取正在进行的升级任务")
    @GetMapping("/task")
    public Result<?> getUpgradeTask() {
        return Result.success(upgradeService.getUpgradeTask());
    }

    /**
     * 执行升级
     */
    @Description("执行升级")
    @PostMapping("/execute")
    public Result execute() {
        upgradeService.execute();
        return Result.success();
    }

    /**
     * 清除升级任务
     */
    @Description("清除升级任务")
    @PostMapping("/clear")
    public Result clearUpgradeTask() {
        upgradeService.clearUpgradeTask();
        return Result.success();
    }

    @PostMapping("/operate/{operate}")
    public Result operate(@PathVariable(value = "operate") String operate) {
        upgradeService.operate(operate);
        return Result.success();
    }
}
