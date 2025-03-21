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
import java.util.Map;

/**
 * 插件管理
 */
@RestController
@RequestMapping("adminapi/addon")
@SaCheckLogin
public class AddonController {

    @Resource
    IAddonService addonService;

    /**
     * 本地插件列表
     *
     * @return
     */
    @GetMapping("/local")
    public Result<LocalAddonListVo> getLocalAddonList() {
        return Result.success(addonService.getLocalAddonList());
    }

    /**
     * 插件列表
     *
     * @param addonSearchParam 搜索参数
     * @return Result<PageResult < Addon>>
     */
    @GetMapping("/list")
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
    @GetMapping("/list/install")
    @SaIgnore
    public Result<Map<String, InstallAddonListVo>> getInstallList() {
        return Result.success(addonService.getInstallList());
    }

    /**
     * 插件详情
     *
     * @param id 主键ID
     * @return Result<AddonInfoVo>
     */
    @GetMapping("/:id")
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
    @PostMapping("/add")
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
    @PostMapping("/del")
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
    @PostMapping("/install/{addon}")
    public Result<JSONObject> install(@PathVariable("addon") String addon) {
        return Result.success(addonService.install(addon, "local"));
    }

    /**
     * 云安装插件
     *
     * @param addon
     * @return
     */
    @PostMapping("/cloudinstall/{addon}")
    public Result<JSONObject> cloudInstall(@PathVariable("addon") String addon) {
        return Result.success(addonService.install(addon, "cloud"));
    }

    /**
     * 云安装进度
     *
     * @param addon
     * @return
     */
    @GetMapping("/cloudinstall/{addon}")
    public Result<JSONObject> cloudInstallLog(@PathVariable("addon") String addon) {
        return Result.success(addonService.cloudInstallLog(addon));
    }

    /**
     * 插件安装检测安装环境
     *
     * @param addon
     * @return
     */
    @GetMapping("/install/check/{addon}")
    public Result<JSONObject> installCheck(@PathVariable("addon") String addon) {
        return Result.success(addonService.installCheck(addon));
    }

    /**
     * 插件安装检测安装环境
     *
     * @param addon
     * @return
     */
    @PutMapping("/install/cancel/{addon}")
    public Result cancleInstall(@PathVariable("addon") String addon) {
        addonService.cancleInstall();
        return Result.success();
    }

    /**
     * 查询安装任务
     *
     * @return
     */
    @GetMapping("/installtask")
    public Result<JSONObject> getInstallTask() {
        return Result.success(addonService.getInstallTask());
    }

    /**
     * 卸载插件
     *
     * @param addon
     * @return
     */
    @PostMapping("/uninstall/{addon}")
    public Result<JSONObject> unnstall(@PathVariable("addon") String addon) {
        return Result.success(addonService.uninstall(addon));
    }

    /**
     * 卸载插件环境检测
     *
     * @param addon
     * @return
     */
    @GetMapping("/uninstall/check/{addon}")
    public Result<JSONObject> uninstallCheck(@PathVariable("addon") String addon) {
        return Result.success(addonService.uninstallCheck(addon));
    }

    /**
     * 插件类型
     *
     * @return
     */
    @GetMapping("/type")
    public Result<Map<String, String>> addonType() {
        return Result.success(AddonTypeEnum.getMap());
    }

    /**
     * 下载插件
     *
     * @param addon
     * @return
     */
    @PostMapping("/download/{addon}")
    public Result<?> download(@PathVariable("addon") String addon, @RequestBody AddonDownloadParam param) {
        addonService.download(addon, param.getVersion());
        return Result.success();
    }
}
