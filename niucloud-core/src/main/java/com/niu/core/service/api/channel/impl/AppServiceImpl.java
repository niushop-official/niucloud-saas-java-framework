package com.niu.core.service.api.channel.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.AppVersion;
import com.niu.core.enums.channel.AppDict;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.AppVersionMapper;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.channel.IAppService;
import com.niu.core.service.api.channel.param.GetNewVersionParam;
import com.niu.core.service.api.channel.vo.ApiAppConfigVo;
import com.niu.core.service.api.channel.vo.NewVersionVo;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.core.channel.ICoreAppService;
import com.niu.core.service.core.channel.vo.AppConfigVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;

@Service
public class AppServiceImpl implements IAppService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    AppVersionMapper appVersionMapper;

    @Resource
    ILoginService loginService;

    @Resource
    IRegisterService registerService;

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    ICoreAppService coreAppService;

    @Resource
    ICoreWeappConfigService coreWeappConfigService;

    @Override
    public LoginVo wechatLogin(WechatAuthParam param) {
        try {
            WxMpService app = WechatUtils.app(RequestUtils.siteId());

            WxOAuth2AccessToken wxOAuth2AccessToken = app.getOAuth2Service().getAccessToken(param.getCode());
            WxOAuth2UserInfo wxUser = app.getOAuth2Service().getUserInfo(wxOAuth2AccessToken, null);

            return this.login(
                ObjectUtil.defaultIfNull(wxUser.getOpenid(), ""),
                ObjectUtil.defaultIfNull(wxUser.getUnionId(), ""),
                ObjectUtil.defaultIfNull(wxUser.getNickname(), ""),
                ObjectUtil.defaultIfNull(wxUser.getHeadImgUrl(), ""),
                param.getPid()
            );
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 登录
     * @param openid
     * @param unionid
     * @param nickname
     * @param avatar
     * @return
     */
    private LoginVo login(String openid, String unionid, String nickname, String avatar, Integer pid) {
        if (openid.isEmpty()) throw new CommonException("openid不能为空");

        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("wxapp_openid", openid).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNotNull(member)) {
            return loginService.login(member);
        }
        if (ObjectUtil.isNotEmpty(unionid)) {
            Member unionidMember = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_unionid", openid).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(unionidMember)) {
                return loginService.login(unionidMember);
            }
        }

        LoginVo loginVo = new LoginVo();
        loginVo.setOpenid(openid);
        loginVo.setUnionid(unionid);
        loginVo.setNickname(nickname);
        loginVo.setAvatar(avatar);

        LoginConfigVo config = coreMemberConfigService.getLoginConfig(RequestUtils.siteId());

        Member registerMember = new Member();
        registerMember.setWxappOpenid(openid);
        registerMember.setNickname(nickname);
        registerMember.setHeadimg(avatar);
        registerMember.setWxUnionid(unionid);
        registerMember.setPid(pid);

        // 开启自动注册会员
        if (config.getIsAuthRegister() == 1) {
            // 开启强制获取会员信息并且开启强制绑定手机号，必须获取手机号才能进行注册，由于公众号无法主动获取，所以不能注册
            if (config.getIsBindMobile() == 1 && config.getIsForceAccessUserInfo() == 1) {
                return loginVo;
            } else if (config.getIsForceAccessUserInfo() == 1) {
                // 开启强制获取会员信息时，必须获取到昵称和头像才能进行注册
                if (ObjectUtil.isNotEmpty(nickname) && ObjectUtil.isNotEmpty(avatar)) {
                    return registerService.register(registerMember);
                } else {
                    return loginVo;
                }
            } else if (config.getIsBindMobile() == 1) {
                // 开启强制绑定手机号，必须获取手机号才能进行注册，由于公众号无法主动获取，所以不能注册
                return loginVo;
            } else {
                return registerService.register(registerMember);
            }
        } else {
            return loginVo;
        }
    }


    @Override
    public NewVersionVo getNewVersion(GetNewVersionParam param) {
        AppVersion appVersion = appVersionMapper.selectOne(new QueryWrapper<AppVersion>()
            .eq("site_id", RequestUtils.siteId())
            .eq("platform", param.getPlatform())
            .gt("version_code", param.getVersionCode())
            .eq("status", AppDict.StatusEnum.STATUS_PUBLISHED.getValue())
            .orderByDesc("version_code"));

        if (appVersion == null) {
            return null;
        }

        NewVersionVo newVersionVo = new NewVersionVo();
        BeanUtil.copyProperties(appVersion, newVersionVo);

        return newVersionVo;
    }

    @Override
    public LoginVo register(AuthRegisterParam param) {
        switch (param.getRegisterType()) {
            case "wechat":
                return wechatRegister(param);
            default:
                return null;
        }
    }

    @Override
    public ApiAppConfigVo getAppConfig() {
        ApiAppConfigVo vo = new ApiAppConfigVo();
        AppConfigVo config = coreAppService.getConfig(RequestUtils.siteId());
        BeanUtil.copyProperties(config, vo);

        WeappConfigVo weappConfig = coreWeappConfigService.getWeappConfig(RequestUtils.siteId());
        vo.setWeappOriginal(weappConfig.getWeappOriginal());
        return vo;
    }

    public LoginVo wechatRegister(AuthRegisterParam param) {
        if (param.getOpenid().isEmpty()) throw new CommonException("openid不能为空");
        try {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("wxapp_openid", param.getOpenid()).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(member)) throw new CommonException("账号已存在");

            if (!param.getUnionid().isEmpty()) {
                Member unionidMember = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_unionid", param.getUnionid()).eq("site_id", RequestUtils.siteId()));
                if (ObjectUtil.isNotNull(unionidMember)) throw new CommonException("账号已存在");
            }

            LoginConfigVo config = coreMemberConfigService.getLoginConfig(RequestUtils.siteId());
            if (config.getIsBindMobile() == 1) {
                if (param.getMobile().isEmpty()) throw new CommonException("手机号不能为空");
                registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
            }

            Member registerMember = new Member();
            registerMember.setWxappOpenid(param.getOpenid());
            registerMember.setMobile(param.getMobile());
            registerMember.setWxUnionid(param.getUnionid());
            registerMember.setPid(param.getPid());
            return registerService.register(registerMember);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
