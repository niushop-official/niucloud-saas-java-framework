package com.niu.core.controller.api.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.IMemberService;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.MemberCenterVo;
import com.niu.core.service.api.member.vo.MemberInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 会员管理
 */
@RestController
@RequestMapping("/api/member")
@Slf4j
public class MemberController {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IMemberLevelService memberLevelService;

    /**
     * 会员信息
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("/member")
    public Result<?> info() {
        MemberInfoParam param = new MemberInfoParam();
        return Result.success(memberService.info(param));
    }

    /**
     * 会员中心
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("/center")
    public Result<?> center() {
        MemberCenterParam param = new MemberCenterParam();
        return Result.success(memberService.center(param));
    }

    /**
     * 会员信息修改
     *
     * @param param
     * @return
     */
    @SaCheckLogin
    @PutMapping("/modify/{field}")
    public Result<?> modify(@PathVariable("field") String field, @RequestBody MemberModifyParam param) {
        param.setField(field);
        memberService.modify(param);
        return Result.success();
    }

    /**
     * 会员信息编辑
     *
     * @param param
     * @return
     */
    @SaCheckLogin
    @PutMapping("/edit")
    public Result<?> edit(@RequestBody MemberEditParam param) {
        memberService.edit(param);
        return Result.success();
    }

    /**
     * 绑定手机号
     *
     * @param param
     * @return
     */
    @SaCheckLogin
    @PutMapping("/mobile")
    public Result<?> mobile(@RequestBody MemberMobileParam param) {
        memberService.mobile(param);
        return Result.success();
    }

    /**
     * 获取会员码
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("/qrcode")
    public Result<?> qrcode() {
        MemberQcodeParam param = ObjectUtils.create(MemberQcodeParam.class);
        return Result.success(memberService.qrcode(param));
    }

    /**
     * 会员日志
     *
     * @param param
     * @return
     */
    @PostMapping("/log")
    public Result<?> log(MemberLogParam param) {
        memberService.log(param);
        return Result.success();
    }

    /**
     * 获取会员级别
     *
     * @return
     */
    @GetMapping("/level")
    public Result<?> level(MemberLevelParam param) {
        return Result.success(memberLevelService.list(param));
    }

    @PutMapping("/getmobile")
    public Result<?> getMobile(@RequestBody MemberGetMobileParam param) {
        return Result.success(memberLevelService.getMobile(param.getMobileCode()));
    }
}
