package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.IMemberSignService;
import com.niu.core.service.admin.member.param.MemberConfigParam;
import com.niu.core.service.admin.member.param.MemberLevelSearchParam;
import com.niu.core.service.admin.member.param.MemberSignSearchParam;
import com.niu.core.service.admin.member.param.SignConfigParam;
import com.niu.core.service.admin.member.vo.MemberSignListVo;
import com.niu.core.service.admin.member.vo.SignConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 会员签到控制器
 */

@RestController
@RequestMapping("adminapi/member/sign")
@SaCheckLogin
public class MemberSignController {

    @Resource
    IMemberSignService memberSignService;

    /**
     * 签到设置
     * @return
     */
    @GetMapping("")
    public Result<PageResult<MemberSignListVo>> pages(@Validated PageParam pageParam, @Validated MemberSignSearchParam searchParam) {
        PageResult<MemberSignListVo> list = memberSignService.pages(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 签到设置
     * @return
     */
    @GetMapping("/config")
    public Result<SignConfigVo> getSignConfig() {
        SignConfigVo config = memberSignService.getSignConfig();
        return Result.success(config);
    }

    /**
     * 签到设置
     * @param configParam
     * @return
     */
    @PutMapping("/config")
    public Result setSignConfig(@Validated @RequestBody SignConfigParam configParam) {
        memberSignService.setSignConfig(configParam);
        return Result.success();
    }

}
