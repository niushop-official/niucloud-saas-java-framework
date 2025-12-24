package com.niu.core.controller.adminapi.verify;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.verify.param.VerifyParam;
import com.niu.core.service.admin.verify.param.VerifySearchParam;
import com.niu.core.service.admin.verify.IVerifyService;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jdk.jfr.Description;

/**
 * 核销记录控制器
 */

@RestController
@RequestMapping("adminapi/verify")
@SaCheckLogin
@Description("核销")
public class VerifyController {

    @Resource
    IVerifyService verifyService;

    /**
     * 核销记录列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<VerifyListVo>>
     */
    @GetMapping("/verify/record")
    @Description("核销记录列表")
    public Result<PageResult<VerifyListVo>> list(@Validated PageParam pageParam, @Validated VerifySearchParam searchParam) {
        PageResult<VerifyListVo> list = verifyService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 核销记录详情
     */
    @GetMapping("/verify/{verify_code}")
    @Description("核销记录详情")
    public Result<VerifyInfoVo> info(@PathVariable("verify_code") String code) {
        VerifyInfoVo info = verifyService.detail(code);
        return Result.success(info);
    }

    /**
     * 核销记录详情
     */
    @GetMapping("/detail/{verify_code}")
    @Description("核销详情")
    public Result getInfoByCode(@PathVariable("verify_code") String code) {
        return Result.success(verifyService.getInfoByCode(code));
    }

    /**
     * 核销
     * @param code
     * @return
     */
    @Description("核销")
    @PostMapping("/verify/{verify_code}")
    public Result verify(@PathVariable("verify_code") String code) {
        verifyService.verify(code);
        return Result.success("核销成功");
    }
}
