package com.niu.core.service.api.login.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.event.EventPublisher;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.member.MemberLoginTypeEnum;
import com.niu.core.enums.member.MemberRegisterTypeEnum;
import com.niu.core.event.member.MemberRegisterEvent;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.param.AccountRegisterParam;
import com.niu.core.service.api.login.param.MobileRegisterParam;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.login.vo.MobileCodeCacheVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.ICoreMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

/**
 * 注册实现
 */
@Service
public class RegisterServiceImpl implements IRegisterService {

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    ICoreMemberService coreMemberService;

    @Resource
    ILoginService loginService;

    @Resource
    MemberMapper memberMapper;

    @Resource
    private Cached cached;

    /**
     * 校验登录注册方式
     * @param type
     */
    @Override
    public void checkLoginConfig(String type) {
        LoginConfigVo config = coreMemberConfigService.getLoginConfig(RequestUtils.siteId());
        if (MemberLoginTypeEnum.USERNAME.getType().equals(type) && config.getIsUsername() != 1) throw new CommonException("未开启账号登录注册");
        if (MemberLoginTypeEnum.MOBILE.getType().equals(type) && config.getIsMobile() != 1) throw new CommonException("未开启手机验证码登录注册");
    }

    /**
     * 账号注册
     * @param param
     * @return
     */
    @Override
    public LoginVo account(AccountRegisterParam param) {
        Member memberExist = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("username", param.getUsername()).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNotNull(memberExist)) throw new CommonException("账号已存在");

        if (!param.getMobile().isEmpty()) {
            Member mobileExist = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("mobile", param.getMobile()).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(mobileExist)) throw new CommonException("当前手机号已绑定账号");
            this.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
        }

        Member member = new Member();
        member.setUsername(param.getUsername());
        member.setNickname(param.getUsername());
        member.setPassword(PasswordEncipher.encode(param.getPassword()));
        member.setMobile(param.getMobile());
        member.setRegisterType(MemberRegisterTypeEnum.USERNAME.getType());
        member.setLoginType(MemberLoginTypeEnum.USERNAME.getType());
        member.setPid(param.getPid());

        return this.register(member);
    }

    /**
     * 手机号注册
     * @param param
     * @return
     */
    @Override
    public LoginVo mobile(MobileRegisterParam param) {
        Member memberExist = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("mobile", param.getMobile()).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNotNull(memberExist)) throw new CommonException("账号已存在");

        Member member = new Member();
        member.setMobile(param.getMobile());
        member.setNickname(param.getMobile().substring(0, 3) + "****" + param.getMobile().substring(7));
        member.setRegisterType(MemberRegisterTypeEnum.MOBILE.getType());
        member.setLoginType(MemberLoginTypeEnum.MOBILE.getType());
        member.setPid(param.getPid());
        member.setHeadimg(ObjectUtil.defaultIfNull(param.getAvatar(), ""));
        member.setNickname(ObjectUtil.defaultIfNull(param.getNickname(), ""));
        member.setWxOpenid(ObjectUtil.defaultIfNull(param.getOpenid(), ""));

        return this.register(member);
    }

    /**
     * 注册
     * @param member
     * @return
     */
    public LoginVo register(Member member) {
        HttpServletRequest request = RequestUtils.handler();
        Integer pid = (Integer) ObjectUtil.defaultIfNull(member.getPid(), 0);
        if (pid > 0) {
            Member inviteMember = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("member_id", member.getPid()).eq("site_id", RequestUtils.siteId()));
            if (inviteMember == null) member.setPid(0);
        }
        if (member.getMobile() != null && !member.getMobile().isEmpty()) {
            Member mobile = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("mobile", member.getMobile()).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(mobile)) throw new CommonException("账号已存在");
        }

        if (ObjectUtil.isEmpty(member.getNickname())) {
            if (ObjectUtil.isNotEmpty(member.getUsername())) {
                member.setNickname(member.getUsername());
            } else if (ObjectUtil.isNotEmpty(member.getMobile())) {
                member.setNickname(member.getMobile().substring(0, 3) + "****" + member.getMobile().substring(7));
            } else {
                member.setNickname(createName());
            }
        }

        member.setSiteId(RequestUtils.siteId());
        member.setCreateTime(System.currentTimeMillis() / 1000);
        member.setMemberNo(coreMemberService.createMemberNo(RequestUtils.siteId()));
        member.setRegisterChannel(RequestUtils.channel());
        this.memberMapper.insert(member);

        // 会员注册事件
        MemberRegisterEvent registerEvent = new MemberRegisterEvent();
        registerEvent.setSiteId(RequestUtils.siteId());
        registerEvent.addAppSign("core");
        registerEvent.setName("MemberRegisterEvent");
        registerEvent.setMember(member);
        EventPublisher.publishEvent(registerEvent);

        return loginService.login(member);
    }

    private String createName() {
        Set<String> set = RandomUtil.randomEleSet(CollUtil.newArrayList("0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W"), 10);
        return String.join("", set);
    }

    /**
     * 校验手机验证码
     * @param mobile
     */
    public void checkMobileCode(String mobile, String mobileKey, String mobileCode) {
        if (mobile.isEmpty()) throw new CommonException("手机号必须填写");
        HttpServletRequest request = RequestUtils.handler();
        if (ObjectUtil.isEmpty(mobileKey) || ObjectUtil.isEmpty(mobileCode)) throw new CommonException("手机验证码有误");
        Object cache = cached.get(mobileKey);
        if (ObjectUtil.isNull(cache)) throw new CommonException("手机验证码有误");
        MobileCodeCacheVo vo = new MobileCodeCacheVo();
        BeanUtils.copyProperties(cache, vo);
        if (!vo.getMobile().equals(mobile) || !vo.getCode().equals(mobileCode)) throw new CommonException("手机验证码有误");
        cached.remove(mobileKey);
    }


}
