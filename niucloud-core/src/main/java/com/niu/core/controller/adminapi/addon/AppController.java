package com.niu.core.controller.adminapi.addon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.service.admin.addon.IAddonLogService;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.addon.param.AddonLogParam;
import com.niu.core.service.admin.addon.param.AddonLogSearchParam;
import com.niu.core.service.admin.addon.param.AddonSearchParam;
import com.niu.core.service.admin.addon.vo.AddonLogInfoVo;
import com.niu.core.service.admin.addon.vo.AddonLogListVo;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件日志管理
 */
@RestController
@RequestMapping("/adminapi")
@SaCheckLogin
public class AppController {

    @Resource
    IAddonService addonService;

    /**
     * 获取安装的应用
     */
    @GetMapping("/app/getAddonList")
    public Result<Map<String, InstallAddonListVo>> getInstallList() {
        Map<String, InstallAddonListVo> appList = new HashMap<>();
        Map<String, InstallAddonListVo> addonList = addonService.getInstallList();
        for (InstallAddonListVo item: addonList.values()) {
            if (item.getType().equals(AddonTypeEnum.APP.getType())) {
                appList.put(item.getKey(), item);
            }
        }
        return Result.success(appList);
    }
}
