package com.niu.core.controller.adminapi.addon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.enums.member.GiftEnum;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.addon.param.AddonDownloadParam;
import com.niu.core.service.admin.addon.param.AddonParam;
import com.niu.core.service.admin.addon.param.AddonSearchParam;
import com.niu.core.service.admin.addon.vo.AddonInfoVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.addon.vo.AddonListVo;
import com.niu.core.service.admin.addon.vo.LocalAddonListVo;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jdk.jfr.Description;

/**
 * 插件管理
 */
@RestController
@RequestMapping("adminapi")
@SaCheckLogin
@Description("插件管理")
public class AddonController {

    @Resource
    IAddonService addonService;

    /**
     * 本地插件列表
     *
     * @return
     */
    @GetMapping("/addon/local")
    @Description("本地插件列表")
    public Result<LocalAddonListVo> getLocalAddonList() {
        return Result.success(addonService.getLocalAddonList());
    }

    /**
     * 插件列表
     *
     * @param addonSearchParam 搜索参数
     * @return Result<PageResult < Addon>>
     */
    @GetMapping("/addon/list")
    @Description("插件列表")
    public Result<PageResult<AddonListVo>> list(@Validated PageParam pageParam, @Validated AddonSearchParam addonSearchParam) {
        PageResult<AddonListVo> list = addonService.list(pageParam, addonSearchParam);
        System.out.println(RequestUtils.adminSiteId());
        return Result.success(list);
    }

    /**
     * 获取已安装插件列表
     *
     * @return Result<PageResult < Addon>>
     */
    @GetMapping("/addon/list/install")
    @SaIgnore
    @Description("获取已安装插件列表")
    public Result<Map<String, InstallAddonListVo>> getInstallList() {
        return Result.success(addonService.getInstallList());
    }

    /**
     * 插件详情
     *
     * @param id 主键ID
     * @return Result<AddonInfoVo>
     */
    @GetMapping("/addon/:id")
    @Description("插件详情")
    public Result<AddonInfoVo> info(@Validated @PathVariable("id") Integer id) {
        AddonInfoVo info = addonService.info(id);
        return Result.success(info);
    }

    /**
     * 插件新增
     *
     * @param addonParam 参数
     * @return Result<Object>
     */
    @PostMapping("/addon/add")
    @Description("插件新增")
    public Result<Object> add(@Validated @RequestBody AddonParam addonParam) {
        addonService.add(addonParam);
        return Result.success();
    }

    /**
     * 插件删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/addon/del")
    @Description("插件删除")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        addonService.del(id);
        return Result.success();
    }

    /**
     * 安装插件
     *
     * @param addon
     * @return
     */
    @PostMapping("/addon/install/{addon}")
    @Description("安装插件")
    public Result<JSONObject> install(@PathVariable("addon") String addon) {
        return Result.success(addonService.install(addon, "local"));
    }

    /**
     * 云安装插件
     *
     * @param addon
     * @return
     */
    @PostMapping("/addon/cloudinstall/{addon}")
    @Description("云安装插件")
    public Result<JSONObject> cloudInstall(@PathVariable("addon") String addon) {
        return Result.success(addonService.install(addon, "cloud"));
    }

    /**
     * 云安装进度
     *
     * @param addon
     * @return
     */
    @GetMapping("/addon/cloudinstall/{addon}")
    @Description("云安装进度")
    public Result<JSONObject> cloudInstallLog(@PathVariable("addon") String addon) {
        return Result.success(addonService.cloudInstallLog(addon));
    }

    /**
     * 插件安装检测安装环境
     *
     * @param addon
     * @return
     */
    @GetMapping("/addon/install/check/{addon}")
    @Description("插件安装检测安装环境")
    public Result<JSONObject> installCheck(@PathVariable("addon") String addon) {
        return Result.success(addonService.installCheck(addon));
    }

    /**
     * 取消安装任务
     *
     * @param addon
     * @return
     */
    @PutMapping("/addon/install/cancel/{addon}")
    @Description("取消安装任务")
    public Result cancleInstall(@PathVariable("addon") String addon) {
        addonService.cancleInstall();
        return Result.success();
    }

    /**
     * 查询安装任务
     *
     * @return
     */
    @GetMapping("/addon/installtask")
    @Description("查询安装任务")
    public Result<JSONObject> getInstallTask() {
        return Result.success(addonService.getInstallTask());
    }

    /**
     * 卸载插件
     *
     * @param addon
     * @return
     */
    @PostMapping("/addon/uninstall/{addon}")
    @Description("卸载插件")
    public Result<JSONObject> uninstall(@PathVariable("addon") String addon) {
        return Result.success(addonService.uninstall(addon));
    }

    /**
     * 卸载插件环境检测
     *
     * @param addon
     * @return
     */
    @GetMapping("/addon/uninstall/check/{addon}")
    @Description("卸载插件环境检测")
    public Result<JSONObject> uninstallCheck(@PathVariable("addon") String addon) {
        return Result.success(addonService.uninstallCheck(addon));
    }

    /**
     * 插件类型
     *
     * @return
     */
    @GetMapping("/addontype")
    @Description("插件类型")
    public Result<Map<String, String>> addonType() {
        return Result.success(AddonTypeEnum.getMap());
    }

    @GetMapping("/addon/init")
    @Description("插件初始化")
    public Result<?> init() {
        Map<String, Object> data = new HashMap<>();
        data.put("type_list", AddonTypeEnum.getMap());
        return Result.success(data);
    }

    /**
     * 下载插件
     *
     * @param addon
     * @return
     */
    @PostMapping("/addon/download/{addon}")
    @Description("下载插件")
    public Result<?> download(@PathVariable("addon") String addon, @RequestBody AddonDownloadParam param) {
        addonService.download(addon, param.getVersion());
        return Result.success();
    }
}
