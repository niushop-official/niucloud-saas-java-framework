package com.niu.core.controller.adminapi.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.diy.IDiyRouteService;
import com.niu.core.service.admin.diy.param.DiyRouteSearchParam;
import com.niu.core.service.admin.diy.param.DiyRouteShareParam;
import com.niu.core.service.admin.diy.vo.DiyRouteInfoVo;
import com.niu.core.service.admin.diy.vo.DiyRouteListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义路由管理
 */
@RestController
@RequestMapping("/adminapi/diy/route")
@SaCheckLogin
public class DiyRouteController {

    @Resource
    IDiyRouteService diyRouteService;

    @Resource
    ICoreAddonService coreAddonService;

    /**
     * 自定义路由列表
     *
     * @param searchParam 搜索参数
     * @return Result<Object>
     */
    @GetMapping("")
    public Result<List<DiyRouteListVo>> list(@Validated DiyRouteSearchParam searchParam) {
        return Result.success(diyRouteService.list(searchParam));
    }

    /**
     * 存在的应用插件列表
     *
     * @return
     */
    @GetMapping("/apps")
    public Result<Map<String, InstallAddonListVo>> info() {
        return Result.success(coreAddonService.getInstallAddonList());
    }

    /**
     * 获取自定义路由分享内容
     *
     */
    @GetMapping("/info")
    public Result<Object> getInfoByName(@RequestParam("name") String name) {
        return Result.success(ObjectUtil.defaultIfNull(diyRouteService.getInfoByName(name), new ArrayList<>()));
    }

    /**
     * 自定义路由编辑
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PutMapping("/share")
    public Result<Object> modifyShare(@Validated @RequestBody DiyRouteShareParam editParam) {
        diyRouteService.modifyShare(editParam);
        return Result.success();
    }

}
