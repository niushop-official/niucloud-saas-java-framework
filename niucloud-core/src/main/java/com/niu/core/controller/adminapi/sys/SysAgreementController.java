package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.ISysAgreementService;
import com.niu.core.service.admin.sys.param.SysAgreementParam;
import com.niu.core.service.admin.sys.param.SysAgreementSearchParam;
import com.niu.core.service.admin.sys.vo.SysAgreementInfoVo;
import com.niu.core.service.admin.sys.vo.SysAgreementListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 协议控制器
 */

@RestController
@RequestMapping("adminapi/sys")
@SaCheckLogin
public class SysAgreementController {

    @Resource
    ISysAgreementService sysAgreementService;

    /**
     * 协议列表
     *
     * @return Result<List<SysAgreementListVo>>
     */
    @GetMapping("/agreement")
    public Result<List<SysAgreementListVo>> list() {
        List<SysAgreementListVo> list = sysAgreementService.list();
        return Result.success(list);
    }

    /**
     * 协议详情
     *
     * @param key
     * @return Result<SysAgreementInfoVo>
     */
    @GetMapping("/agreement/{key}")
    public Result<SysAgreementInfoVo> info(@Validated @PathVariable("key") String key) {
        SysAgreementInfoVo info = sysAgreementService.getAgreement(key);
        return Result.success(info);
    }

    /**
     * 协议编辑
     *
     * @param key
     * @param sysAgreementParam
     * @return
     */
    @PutMapping("/agreement/{key}")
    public Result<Object> edit(@PathVariable("key") String key, @RequestBody SysAgreementParam sysAgreementParam)  {
        sysAgreementService.setAgreement(key, sysAgreementParam.getTitle(), sysAgreementParam.getContent());
        return Result.success();
    }

}
