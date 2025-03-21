package com.niu.core.controller.adminapi.addon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.addon.IAddonDevelopBuildService;
import com.niu.core.service.admin.addon.IAddonDevelopService;
import com.niu.core.service.admin.addon.param.AddonDevelopAddParam;
import com.niu.core.service.admin.addon.param.AddonDevelopSearchParam;
import com.niu.core.service.admin.addon.vo.AddonDevelopInfoVo;
import com.niu.core.service.admin.addon.vo.AddonDevelopListVo;
import com.niu.core.service.admin.niucloud.INiucloudService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件开发
 */
@RestController
@RequestMapping("/adminapi/addon_develop")
@SaCheckLogin
public class AddonDevelopController {

    @Resource
    IAddonDevelopBuildService addonDevelopBuildService;

    @Resource
    IAddonDevelopService addonDevelopService;

    @Resource
    INiucloudService niucloudService;

    /**
     * 插件打包
     * @return Result<Object>
     */
    @PostMapping("/build/{key}")
    public Result build(@PathVariable("key") String key) {
        addonDevelopBuildService.build(key);
        return Result.success();
    }

    /**
     * 开发插件列表
     */
    @GetMapping("")
    public Result<List<AddonDevelopListVo>> list(@Validated AddonDevelopSearchParam searchParam) {
        List<AddonDevelopListVo> list = addonDevelopService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 查询插件
     */
    @GetMapping("/{key}")
    public Result<AddonDevelopInfoVo> info(@PathVariable("key") String key) {
        return Result.success(addonDevelopService.info(key));
    }

    /**
     * 新增插件
     */
    @PostMapping("/{key}")
    public Result add(@PathVariable("key") String key, @Validated @RequestBody AddonDevelopAddParam param) {
        addonDevelopService.add(param);
        return Result.success();
    }

    /**
     * 编辑插件
     */
    @PutMapping("/{key}")
    public Result edit(@PathVariable("key") String key, @Validated @RequestBody AddonDevelopAddParam param) {
        addonDevelopService.edit(param);
        return Result.success();
    }

    /**
     * 删除插件
     */
    @DeleteMapping("/{key}")
    public Result del(@PathVariable("key") String key) {
        addonDevelopService.del(key);
        return Result.success();
    }

    /**
     * 查询插件key是否存在
     */
    @GetMapping("/check/{key}")
    public Result checkKey(@PathVariable("key") String key) {
        return Result.success(niucloudService.checkKey(key));
    }

    /**
     * 查询插件名黑名单
     */
    @GetMapping("/key/blacklist")
    public Result<String[]> keyBlackList() {
        String[] blackList = {"addon", "aliapp", "app"};
        return Result.success(blackList);
    }

    /**
     * 下载插件
     * @param key
     * @return
     */
    @PostMapping("/download/{key}")
    public Result download(@PathVariable("key") String key) {
        return Result.success("", addonDevelopBuildService.download(key));
    }
}
