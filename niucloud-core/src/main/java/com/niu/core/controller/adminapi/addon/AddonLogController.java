package com.niu.core.controller.adminapi.addon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.addon.IAddonLogService;
import com.niu.core.service.admin.addon.param.AddonLogParam;
import com.niu.core.service.admin.addon.param.AddonLogSearchParam;
import com.niu.core.service.admin.addon.vo.AddonLogInfoVo;
import com.niu.core.service.admin.addon.vo.AddonLogListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 插件日志管理
 */
@RestController
@RequestMapping("/api/addon_log")
@SaCheckLogin
public class AddonLogController {

    @Resource
    IAddonLogService addonLogService;

    /**
     * 插件日志列表
     *
     * @param pageParam        分页参数
     * @param addonLogSearchParam 搜索参数
     * @return Result<Object>
     */
    @GetMapping("/list")
    public Result<PageResult<AddonLogListVo>> list(@Validated PageParam pageParam,
                                                   @Validated AddonLogSearchParam addonLogSearchParam) {
        PageResult<AddonLogListVo> list = addonLogService.list(pageParam, addonLogSearchParam);
        return Result.success(list);
    }

    /**
     * 插件日志详情
     *
     * @param id 主键ID
     * @return Result<AddonLogDetailVo>
     */
    @GetMapping("/detail")
    public Result<AddonLogInfoVo> info(@Validated @RequestParam("id") Integer id) {
        AddonLogInfoVo detail = addonLogService.detail(id);
        return Result.success(detail);
    }

    /**
     * 插件日志新增
     *
     * @param addonLogParam 参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody AddonLogParam addonLogParam) {
        addonLogService.add(addonLogParam);
        return Result.success();
    }

    /**
     * 插件日志删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        addonLogService.del(id);
        return Result.success();
    }

}
