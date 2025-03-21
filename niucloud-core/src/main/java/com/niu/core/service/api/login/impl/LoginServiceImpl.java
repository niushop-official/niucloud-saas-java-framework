package com.niu.core.service.api.login.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.event.EventPublisher;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.member.MemberLoginTypeEnum;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.event.member.MemberLoginEvent;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.param.*;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.login.vo.MobileCodeCacheVo;
import com.niu.core.service.api.login.vo.SendMobileCodeVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.notice.ICoreNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册实现
 */
@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    ICoreNoticeService coreNoticeService;

    @Resource
    private Cached cached;

    IRegisterService registerService;

    @Resource
    public void setRegisterService(@Lazy IRegisterService registerService) {
        this.registerService = registerService;
    }

    /**
     * 账号登录
     * @param param
     * @return
     */
    public LoginVo account(AccountLoginParam param) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("site_id", RequestUtils.siteId()).and(i -> i.eq("username", param.getUsername()).or().eq("mobile", param.getUsername())));

        if (ObjectUtil.isNull(member)) throw new CommonException("账号不存在");
        if (!PasswordEncipher.matche(param.getPassword(), member.getPassword())) throw new CommonException("账号或密码错误");
        member.setLoginType(MemberLoginTypeEnum.USERNAME.getType());

        return this.login(member);
    }

    /**
     * 手机号登录
     * @param param
     * @return
     */
    public LoginVo mobile(MobileLoginParam param) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("mobile", param.getMobile()).eq("site_id", RequestUtils.siteId()));

        if (member != null) {
            member.setLoginType(MemberLoginTypeEnum.MOBILE.getType());
            return this.login(member);
        } else {
            LoginConfigVo config = coreMemberConfigService.getLoginConfig(RequestUtils.siteId());
            // 开启强制绑定手机号 登录会自动注册
            if (config.getIsBindMobile() == 1) {
                MobileRegisterParam registerParam = new MobileRegisterParam();
                BeanUtils.copyProperties(param, registerParam);
                return registerService.mobile(registerParam);
            }
        }
        throw new CommonException("账号不存在");
    }

    /**
     * 登录
     * @param member
     * @return
     */
    public LoginVo login(Member member) {
        member = memberMapper.selectById(member.getMemberId());
        if (StatusEnum.OFF.equals(member.getStatus())) throw new CommonException("账号被锁定");

        member.setLoginTime(System.currentTimeMillis() / 1000);
        member.setLoginIp(RequestUtils.ip());
        member.setLoginCount(member.getLoginCount() + 1);
        member.setLoginChannel(RequestUtils.channel());
        if (member.getLoginType() != null) member.setLoginType(member.getLoginType());
        member.setLastVisitTime(System.currentTimeMillis() / 1000);
        this.memberMapper.updateById(member);

        SaLoginModel loginModel = SaLoginModel.create();
        loginModel.setDevice(RequestUtils.handler().getHeader("User-Agent"));
        loginModel.setExtra("memberId", member.getMemberId());
        loginModel.setExtra("username", member.getUsername());
        loginModel.setExtra("siteId", member.getSiteId());
        // 执行登录
        StpUtil.login(member.getMemberId(), loginModel);
        // 获取返回内容
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();

        LoginVo vo = new LoginVo();
        vo.setToken(saTokenInfo.getTokenValue());
        vo.setExpiresTime(DateUtils.currTime()+saTokenInfo.getTokenTimeout());
        vo.setMobile(member.getMobile());

        // 会员登录事件
        MemberLoginEvent loginEvent = new MemberLoginEvent();
        loginEvent.setSiteId(RequestUtils.siteId());
        loginEvent.addAppSign("core");
        loginEvent.setName("MemberLoginEvent");
        EventPublisher.publishEvent(loginEvent);

        return vo;
    }

    /**
     * 重置密码
     * @param param
     */
    public void resetPassword(ResetPasswordParam param) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("mobile", param.getMobile()).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNull(member)) throw new CommonException("当前填写的手机号不存在账号");

        member.setPassword(PasswordEncipher.encode(param.getPassword()));
        this.memberMapper.updateById(member);
    }

    /**
     * 获取登录注册配置
     * @return
     */
    public LoginConfigVo getLoginConfig() {
        return coreMemberConfigService.getLoginConfig(RequestUtils.siteId());
    }

    /**
     * 发送手机验证码
     * @param param
     */
    public SendMobileCodeVo sendMobileCode(SendMobileCodeParam param) {
        String key = RandomUtil.randomString(30);

        MobileCodeCacheVo cache = new MobileCodeCacheVo();
        cache.setMobile(param.getMobile());
        cache.setCode(String.format("%0" + 4 + "d", RandomUtil.randomInt(1, 9999)));
        cache.setType(param.getType());

        Map<String, Object> data = new HashMap<>();
        data.put("mobile", param.getMobile());
        data.put("code", cache.getCode());

        coreNoticeService.send(param.siteId(), "member_verify_code", data);

        this.cached.put(key, cache, 600);

        SendMobileCodeVo vo = new SendMobileCodeVo();
        vo.setKey(key);
        return vo;
    }

    /**
     * 退出登录
     *
     */
    public void logout() {
        StpUtil.logout();
    }
}
