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

/**
 * 核销记录控制器
 */

@RestController
@RequestMapping("adminapi/verify/verify")
@SaCheckLogin
public class VerifyController {

    @Resource
    IVerifyService verifyService;

    /**
     * 核销记录列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<VerifyListVo>>
     */
    @GetMapping("/record")
    public Result<PageResult<VerifyListVo>> list(@Validated PageParam pageParam, @Validated VerifySearchParam searchParam) {
        PageResult<VerifyListVo> list = verifyService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 核销记录详情
     */
    @GetMapping("/{verify_code}")
    public Result<VerifyInfoVo> info(@PathVariable("verify_code") String code) {
        VerifyInfoVo info = verifyService.detail(code);
        return Result.success(info);
    }

}
