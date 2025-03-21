package com.niu.core.service.api.wechat.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.wechat.IWechatService;
import com.niu.core.service.api.wechat.param.WechatAuthDataLoginParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatSyncParam;
import com.niu.core.service.api.wechat.vo.WechatCodeUrlVo;
import com.niu.core.service.api.wechat.vo.WechatScanLoginVo;
import com.niu.core.service.api.wechat.vo.WechatUserInfoVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.sys.ICoreScanService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WechatServiceImpl implements IWechatService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    ILoginService loginService;

    @Resource
    IRegisterService registerService;

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    ICoreScanService coreScanService;

    @Resource
    public void setCoreScanService(@Lazy ICoreScanService coreScanService) {
        this.coreScanService = coreScanService;
    }

    public WechatCodeUrlVo getCodeUrl(String url, String scopes) {
        String authorizationUrl = WechatUtils.mp(RequestUtils.siteId()).getOAuth2Service().buildAuthorizationUrl(url, scopes, "");
        WechatCodeUrlVo vo = new WechatCodeUrlVo();
        vo.setUrl(authorizationUrl);
        return vo;
    }

    /**
     * 公众号登录
     * @param param
     * @return
     */
    public LoginVo loginByCode(WechatAuthParam param) {
        try {
            WxOAuth2AccessToken wxOAuth2AccessToken = WechatUtils.mp(RequestUtils.siteId()).getOAuth2Service().getAccessToken(param.getCode());
            WxOAuth2UserInfo wxUser = WechatUtils.mp(RequestUtils.siteId()).getOAuth2Service().getUserInfo(wxOAuth2AccessToken, null);

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
     *
     * @param param
     * @return
     */
    public LoginVo wechatLogin(WechatAuthDataLoginParam param) {
        JSONObject data = JSONUtil.parseObj(param.getData());

        return this.login(
                data.getStr("openid", ""),
                data.getStr("unionid", ""),
                data.getStr("nickname", ""),
                data.getStr("avatar", ""),
                param.getPid()
        );
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

        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_openid", openid).eq("site_id", RequestUtils.siteId()));
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
        registerMember.setWxOpenid(openid);
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

    /**
     * 授权信息注册账号
     * @param param
     * @return
     */
    public LoginVo register(AuthRegisterParam param) {
        if (param.getOpenid().isEmpty()) throw new CommonException("openid不能为空");
        try {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_openid", param.getOpenid()).eq("site_id", RequestUtils.siteId()));
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
            registerMember.setWxOpenid(param.getOpenid());
            registerMember.setMobile(param.getMobile());
            registerMember.setWxUnionid(param.getUnionid());
            registerMember.setPid(param.getPid());
            return registerService.register(registerMember);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 公众号通过code同步授权
     * @param param
     */
    public void sync(WechatSyncParam param) {
        try {
            WxOAuth2AccessToken wxOAuth2AccessToken = WechatUtils.mp(RequestUtils.siteId()).getOAuth2Service().getAccessToken(param.getCode());
            WxOAuth2UserInfo wxUser = WechatUtils.mp(RequestUtils.siteId()).getOAuth2Service().getUserInfo(wxOAuth2AccessToken, null);

            Member member = new Member();
            member.setMemberId(RequestUtils.memberId());
            member.setNickname(ObjectUtil.defaultIfNull(wxUser.getNickname(), ""));
            member.setHeadimg(ObjectUtil.defaultIfNull(wxUser.getHeadImgUrl(), ""));
            this.memberMapper.updateById(member);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取网页授权信息
     * @param param
     * @return
     */
    public WechatUserInfoVo getWechatUser(WechatAuthParam param) {
        try {
            WechatUserInfoVo vo = new WechatUserInfoVo();

            WxMpService mp = WechatUtils.mp(RequestUtils.siteId());

            WxOAuth2AccessToken wxOAuth2AccessToken = mp.getOAuth2Service().getAccessToken(param.getCode());
            if (wxOAuth2AccessToken.getScope().equals("snsapi_base")) {
                vo.setOpenid(ObjectUtil.defaultIfNull(wxOAuth2AccessToken.getOpenId(), ""));
            } else {
                WxOAuth2UserInfo userInfo = mp.getOAuth2Service().getUserInfo(wxOAuth2AccessToken, null);
                vo.setOpenid(ObjectUtil.defaultIfNull(userInfo.getOpenid(), ""));
                vo.setUnionid(ObjectUtil.defaultIfNull(userInfo.getUnionId(), ""));
                vo.setNickname(ObjectUtil.defaultIfNull(userInfo.getNickname(), ""));
                vo.setAvatar(ObjectUtil.defaultIfNull(userInfo.getHeadImgUrl(), ""));
            }
            return vo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取jssdk config
     * @param url
     * @return
     */
    public Map<String, Object> getJssdkConfig(String url) {
        try {
            WxJsapiSignature result = WechatUtils.mp(RequestUtils.siteId()).createJsapiSignature(url);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("appId", result.getAppId());
            data.put("timestamp", result.getTimestamp());
            data.put("nonceStr", result.getNonceStr());
            data.put("signature", result.getSignature());
            return data;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 扫码登录
     */
    public WechatScanLoginVo scanLogin() {
        try {
            String key = coreScanService.scan(RequestUtils.siteId(), "wechat_login", new JSONObject().set("channel", RequestUtils.channel()), 300);
            WxMpQrCodeTicket ticket = WechatUtils.mp(RequestUtils.siteId()).getQrcodeService().qrCodeCreateTmpTicket(key, 300);
            String url = WechatUtils.mp(RequestUtils.siteId()).getQrcodeService().qrCodePictureUrl(ticket.getTicket());

            WechatScanLoginVo vo = new WechatScanLoginVo();
            vo.setKey(key);
            vo.setUrl(url);

            return vo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 公众号更新用户openid
     * @param param
     */
    public void updateOpenid(WechatAuthParam param) {
        try {
            WxOAuth2AccessToken wxOAuth2AccessToken = WechatUtils.mp(RequestUtils.siteId()).getOAuth2Service().getAccessToken(param.getCode());
            WxOAuth2UserInfo wxUser = WechatUtils.mp(RequestUtils.siteId()).getOAuth2Service().getUserInfo(wxOAuth2AccessToken, null);

            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_openid", wxUser.getOpenid()).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(member)) throw new CommonException("账号已存在");

            Member updateMember = new Member();
            updateMember.setMemberId(RequestUtils.memberId());
            updateMember.setWxOpenid(wxUser.getOpenid());
            this.memberMapper.updateById(updateMember);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
