package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.sys.ISysVerifyService;
import com.niu.core.service.api.sys.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class SysVerifyController {

    @Autowired
    private ISysVerifyService sysVerifyService;

    /**
     * 根据业务获取核销码
     *
     * @param param
     * @return
     */
    @GetMapping("/verify")
    public Result<?> verify(SysVerifyGetCodeParam param) {
        return Result.success(sysVerifyService.getVerifyCode(param));
    }

    /**
     * 校验当前会员是否是核销员
     *
     * @param param
     * @return
     */
    @GetMapping("/check_verifier")
    public Result<?> checkVerifier(SysVerifyCheckVerifierParam param) {
        boolean result = sysVerifyService.checkVerifier(param);
        if (result) {
            return Result.success();
        }
        return Result.fail();
    }

    /**
     * 核销记录
     *
     * @param param
     * @return
     */
    @GetMapping("/verify_records")
    public Result<?> verifyRecords(PageParam pageParam, SysVerifyRecordsParam param) {
        return Result.success(sysVerifyService.records(pageParam, param));
    }

    /**
     * 获取核销详情
     *
     * @param code
     * @return
     */
    @GetMapping("/verify_detail/{code}")
    public Result<?> verifyDetail(@PathVariable("code") Integer code) {
        SysVerifyDetailParam param = new SysVerifyDetailParam();
        param.setCode(code);
        return Result.success(sysVerifyService.detail(param));
    }

    /**
     * 通过code码获取核销信息
     *
     * @param code
     * @return
     */
    @GetMapping("/get_verify_by_code/{code}")
    public Result<?> getVerifyByCode(@PathVariable("code") String code) {
        SysVerifyByCodeParam param = new SysVerifyByCodeParam();
        param.setCode(code);
        return Result.success(sysVerifyService.getInfoByCode(param));
    }

    /**
     * 核销操作
     *
     * @param code
     * @return
     */
    @PostMapping("/verify/{code}")
    public Result<?> verifyCode(@PathVariable("code") String code) {
        SysVerifyCodeParam param = new SysVerifyCodeParam();
        param.setCode(code);
        boolean result = sysVerifyService.verifyCode(param);
        if (result) {
            return Result.success();
        }
        return Result.fail();
    }

}
