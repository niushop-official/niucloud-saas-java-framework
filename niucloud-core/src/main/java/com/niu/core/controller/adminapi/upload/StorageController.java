package com.niu.core.controller.adminapi.upload;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.loader.upload.UploadLoader;
import com.niu.core.service.admin.sys.ISysUserLogService;
import com.niu.core.service.admin.sys.param.SysUserLogSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserLogListVo;
import com.niu.core.service.admin.upload.IStorageConfigService;
import com.niu.core.service.core.upload.vo.CoreStorAgeConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 管理员操作记录管理
 */
@RestController
@RequestMapping("/adminapi/sys/")
@SaCheckLogin
public class StorageController {

    @Resource
    IStorageConfigService storageConfigService;
    @Resource
    ISysUserLogService sysUserLogService;

    /**
     * 系统存储方式列表
     *
     * @return Result<Object>
     */
    @GetMapping("/storage")
    public Result<List<CoreStorAgeConfigVo>> storAgeList() {
        return Result.success(storageConfigService.getStorageList());
    }

    /**
     * 查询配置详情
     * @param storageType
     * @return
     */
    @GetMapping("/storage/{storageType}")
    public Result<CoreStorAgeConfigVo> storageConfig(@Validated @PathVariable("storageType") String storageType){
        return Result.success(storageConfigService.getStorageConfig(storageType));
    }

    /**
     * 编辑配置信息
     * @param storageType
     * @param jsonObject
     * @return
     */
    @PutMapping("/storage/{storageType}")
    public Result editStorage(@Validated @PathVariable("storageType") String storageType, @RequestBody JSONObject jsonObject) {
        storageConfigService.setStorageConfig(storageType, jsonObject);
        return Result.success();
    }

    @GetMapping("/log/{id}")
    public Result<SysUserLogInfoVo> info(@Validated @PathVariable("id") Integer id) {
        SysUserLogInfoVo info = sysUserLogService.info(id);
        return Result.success(info);
    }


}
