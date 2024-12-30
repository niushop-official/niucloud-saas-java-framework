package com.niu.core.service.api.weapp.impl;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.SysNotice;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysNoticeMapper;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.weapp.IWeappService;
import com.niu.core.service.api.wechat.IWechatService;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthDataLoginParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.api.wechat.param.WechatSyncParam;
import com.niu.core.service.api.wechat.vo.WechatCodeUrlVo;
import com.niu.core.service.api.wechat.vo.WechatScanLoginVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.sys.ICoreScanService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeappServiceImpl implements IWeappService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    ILoginService loginService;

    @Resource
    IRegisterService registerService;

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    SysNoticeMapper sysNoticeMapper;

    /**
     * 小程序登录
     * @param param
     * @return
     */
    public LoginVo loginByCode(WechatAuthParam param) {
        try {
            WxMaJscode2SessionResult wxUser = WechatUtils.miniapp(RequestUtils.siteId()).getUserService().getSessionInfo(param.getCode());

            return this.login(
                    ObjectUtil.defaultIfNull(wxUser.getOpenid(), ""),
                    ObjectUtil.defaultIfNull(wxUser.getUnionid(), ""),
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
     * @return
     */
    private LoginVo login(String openid, String unionid, Integer pid) {
        if (openid.isEmpty()) throw new CommonException("openid不能为空");

        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("weapp_openid", openid).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNotNull(member)) {
            return loginService.login(member);
        }
        if (!unionid.isEmpty()) {
            Member unionidMember = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_unionid", openid).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(unionidMember)) {
                return loginService.login(unionidMember);
            }
        }

        Member registerMember = new Member();
        registerMember.setWxOpenid(openid);
        registerMember.setWxUnionid(unionid);
        registerMember.setPid(pid);
        return registerService.register(registerMember);
    }

    /**
     * 授权信息注册账号
     * @param param
     * @return
     */
    public LoginVo register(AuthRegisterParam param) {
        if (param.getOpenid().isEmpty()) throw new CommonException("openid不能为空");
        try {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("weapp_openid", param.getOpenid()).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(member)) throw new CommonException("账号已存在");

            if (!param.getUnionid().isEmpty()) {
                Member unionidMember = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_unionid", param.getUnionid()).eq("site_id", RequestUtils.siteId()));
                if (ObjectUtil.isNotNull(unionidMember)) throw new CommonException("账号已存在");
            }

            LoginConfigVo config = coreMemberConfigService.getLoginConfig(RequestUtils.siteId());
            if (config.getIsBindMobile() == 1) {
                if (!param.getMobile().isEmpty()) {
                    registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
                } else if (!param.getMobileCode().isEmpty()) {
                    WxMaPhoneNumberInfo phoneInfo = WechatUtils.miniapp(RequestUtils.siteId()).getUserService().getPhoneNoInfo(param.getMobileCode());
                    param.setMobile(phoneInfo.getPurePhoneNumber());
                } else {
                    throw new CommonException("手机号不能为空");
                }
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
     * 小程序更新用户openid
     * @param param
     */
    public void updateOpenid(WechatAuthParam param) {
        try {
            WxMaJscode2SessionResult wxUser = WechatUtils.miniapp(RequestUtils.siteId()).getUserService().getSessionInfo(param.getCode());

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

    /**
     *
     * @param keys
     * @return
     */
    public List<String> subscribeMessage(String keys) {
        return sysNoticeMapper.selectList(new QueryWrapper<SysNotice>()
                .select("weapp_template_id")
                        .in("`key`", keys.split(","))
                .eq("is_weapp", 1)
                .eq("site_id", RequestUtils.siteId())
                .ne("weapp_template_id", "")
        ).stream().map(item -> item.getWeappTemplateId()).collect(Collectors.toList());
    }

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    public Map<String, Boolean> getIsTradeManaged() {
        Boolean isTradeManaged = false;

        try {
            String appid = WechatUtils.miniapp(RequestUtils.siteId()).getWxMaConfig().getAppid();
            WxMaOrderShippingIsTradeManagedResponse res = WechatUtils.miniapp(RequestUtils.siteId()).getWxMaOrderShippingService().isTradeManaged(appid);
            isTradeManaged = res.getTradeManaged();
        } catch (Exception e) {
        }

        Map<String, Boolean> map = new HashMap<>();
        map.put("is_trade_managed", isTradeManaged);
        return map;
    }
}
