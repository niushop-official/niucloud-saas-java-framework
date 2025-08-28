package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.IMemberConfigService;
import com.niu.core.service.admin.member.IMemberSignService;
import com.niu.core.service.admin.member.param.*;
import com.niu.core.service.admin.member.vo.*;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 会员配置控制器
 */
@RestController
@RequestMapping("adminapi/member/config")
@SaCheckLogin
@Description("会员设置")
public class MemberConfigController {

    @Resource
    IMemberConfigService memberConfigService;

    /**
     *获取注册与登录设置
     * @return
     */
    @Description("获取注册与登录设置")
    @GetMapping("/login")
    public Result<LoginConfigVo> getLoginConfig() {
        LoginConfigVo config = memberConfigService.getLoginConfig();
        return Result.success(config);
    }

    /**
     * 设置注册与登录设置
     * @param configParam
     * @return
     */
    @PostMapping("/login")
    @Description("设置注册与登录设置")
    public Result setLoginConfig(@Validated @RequestBody LoginConfigParam configParam) {
        memberConfigService.setLoginConfig(configParam);
        return Result.success();
    }

    /**
     *获取会员提现设置
     * @return
     */
    @GetMapping("/cash_out")
    @Description("获取会员提现设置")
    public Result<CashOutConfigVo> getCashOutConfig() {
        CashOutConfigVo config = memberConfigService.getCashOutConfig();
        return Result.success(config);
    }

    /**
     * 获取设置
     * @param configParam
     * @return
     */
    @PostMapping("/cash_out")
    @Description("设置会员提现设置")
    public Result setCashOutConfig(@Validated @RequestBody CashOutConfigParam configParam) {
        memberConfigService.setCashOutConfig(configParam);
        return Result.success();
    }

    /**
     *获取会员设置
     * @return
     */
    @GetMapping("/member")
    @Description("获取会员配置")
    public Result<MemberConfigVo> getMemberConfig() {
        MemberConfigVo config = memberConfigService.getMemberConfig();
        return Result.success(config);
    }

    /**
     * 会员设置
     * @param configParam
     * @return
     */
    @PostMapping("/member")
    @Description("设置会员配置")
    public Result setMemberConfig(@Validated @RequestBody MemberConfigParam configParam) {
        memberConfigService.setMemberConfig(configParam);
        return Result.success();
    }

    /**
     * 设置成长值规则
     */
    @GetMapping("/growth_rule")
    @Description("获取成长值规则")
    public Result<JSONObject> getGrowthRuleConfig() {
        JSONObject config = memberConfigService.getGrowthRuleConfig();
        return Result.success(config);
    }

    /**
     * 设置成长值规则
     */
    @PostMapping("/growth_rule")
    @Description("设置成长值规则")
    public Result setGrowthRuleConfig(@Validated @RequestBody JSONObject configParam) {
        memberConfigService.setGrowthRuleConfig(configParam);
        return Result.success();
    }

    /**
     * 设置成长值规则
     */
    @GetMapping("/point_rule")
    @Description("获取积分规则")
    public Result<JSONObject> getPointRuleConfig() {
        JSONObject config = memberConfigService.getPointRuleConfig();
        return Result.success(config);
    }

    /**
     * 设置积分规则
     */
    @PostMapping("/point_rule")
    @Description("设置积分规则")
    public Result setPointRuleConfig(@Validated @RequestBody JSONObject configParam) {
        memberConfigService.setPointRuleConfig(configParam);
        return Result.success();
    }
}
