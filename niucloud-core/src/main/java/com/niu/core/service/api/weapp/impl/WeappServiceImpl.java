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
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.sys.SysNotice;
import com.niu.core.event.order.WapOrderDetailPathDefiner;
import com.niu.core.event.pay.PayTradeInfoDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
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
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.sys.ICoreScanService;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
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

    @Resource
    ICoreWeappDeliveryService coreWeappDeliveryService;

    @Resource
    PayMapper payMapper;

    /**
     * 小程序登录
     * @param param
     * @return
     */
    public LoginVo loginByCode(WechatAuthParam param) {
        try {
            WxMaJscode2SessionResult wxUser = WechatUtils.miniapp(RequestUtils.siteId()).getUserService().getSessionInfo(param.getCode());

            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("weapp_openid", wxUser.getOpenid()).eq("site_id", RequestUtils.siteId()));
            if (member == null && ObjectUtil.isNotEmpty(wxUser.getUnionid())) {
                member = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_unionid", wxUser.getUnionid()).eq("site_id", RequestUtils.siteId()));
            }
            if (member != null) {
                if (ObjectUtil.isEmpty(member.getNickname()) && ObjectUtil.isNotEmpty(param.getNickname())) member.setNickname(param.getNickname());
                if (ObjectUtil.isEmpty(member.getHeadimg()) && ObjectUtil.isNotEmpty(param.getHeadimg())) member.setHeadimg(param.getHeadimg());
                if (ObjectUtil.isEmpty(member.getWxUnionid()) && ObjectUtil.isNotEmpty(wxUser.getUnionid())) member.setWxUnionid(wxUser.getUnionid());
                return loginService.login(member);
            }

            LoginVo loginVo = new LoginVo();
            loginVo.setOpenid(wxUser.getOpenid());
            loginVo.setUnionid(ObjectUtil.defaultIfNull(wxUser.getUnionid(), ""));

            LoginConfigVo config = coreMemberConfigService.getLoginConfig(RequestUtils.siteId());

            AuthRegisterParam registerParam = new AuthRegisterParam();
            registerParam.setOpenid(wxUser.getOpenid());
            registerParam.setUnionid(ObjectUtil.defaultIfNull(wxUser.getUnionid(), ""));
            registerParam.setPid(param.getPid());
            registerParam.setHeadimg(param.getHeadimg());
            registerParam.setNickname(param.getNickname());
            registerParam.setMobile(param.getMobile());
            registerParam.setMobileCode(param.getMobileCode());

            // 开启自动注册会员
            if (config.getIsAuthRegister() == 1) {
                if (config.getIsBindMobile() == 1 && config.getIsForceAccessUserInfo() == 1) {
                    if (ObjectUtil.isNotEmpty(param.getMobile()) && ObjectUtil.isNotEmpty(param.getHeadimg()) && ObjectUtil.isNotEmpty(param.getNickname())) {
                        return this.register(registerParam);
                    }
                } else if (config.getIsForceAccessUserInfo() == 1) {
                    if (ObjectUtil.isNotEmpty(param.getHeadimg()) && ObjectUtil.isNotEmpty(param.getNickname())) {
                        return this.register(registerParam);
                    }
                } else if (config.getIsBindMobile() == 1) {
                    if (ObjectUtil.isNotEmpty(param.getMobile()) || ObjectUtil.isNotEmpty(param.getMobileCode())) {
                        return this.register(registerParam);
                    }
                } else {
                    return this.register(registerParam);
                }
            } else {
                // 关闭自动注册，但是开启了强制绑定手机号，必须获取手机号才能进行注册
                if (config.getIsBindMobile() == 1 || config.getIsMobile() == 1) {
                    if (ObjectUtil.isNotEmpty(param.getMobile()) || ObjectUtil.isNotEmpty(param.getMobileCode())) {
                        return this.register(registerParam);
                    }
                }
            }

            return loginVo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
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
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("weapp_openid", param.getOpenid()).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(member)) throw new CommonException("账号已存在");

            if (!param.getUnionid().isEmpty()) {
                Member unionidMember = memberMapper.selectOne(new QueryWrapper<Member>().eq("wx_unionid", param.getUnionid()).eq("site_id", RequestUtils.siteId()));
                if (ObjectUtil.isNotNull(unionidMember)) throw new CommonException("账号已存在");
            }

            LoginConfigVo config = coreMemberConfigService.getLoginConfig(RequestUtils.siteId());
            if (config.getIsBindMobile() == 1) {
                if (!param.getMobile().isEmpty() || !param.getMobileCode().isEmpty()) {
                    if (!param.getMobileCode().isEmpty()) {
                        WxMaPhoneNumberInfo phoneInfo = WechatUtils.miniapp(RequestUtils.siteId()).getUserService().getPhoneNoInfo(param.getMobileCode());
                        param.setMobile(phoneInfo.getPurePhoneNumber());
                    }
                } else {
                    throw new CommonException("手机号不能为空");
                }
            }

            Member registerMember = new Member();
            registerMember.setWeappOpenid(param.getOpenid());
            registerMember.setMobile(param.getMobile());
            registerMember.setWxUnionid(param.getUnionid());
            registerMember.setPid(param.getPid());
            registerMember.setNickname(param.getNickname());
            registerMember.setHeadimg(param.getHeadimg());
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

            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("weapp_openid", wxUser.getOpenid()).eq("site_id", RequestUtils.siteId()));
            if (ObjectUtil.isNotNull(member)) throw new CommonException("账号已存在");

            Member updateMember = new Member();
            updateMember.setMemberId(RequestUtils.memberId());
            updateMember.setWeappOpenid(wxUser.getOpenid());
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
    public IsTradeManagedVo getIsTradeManaged() {
        return coreWeappDeliveryService.getIsTradeManaged(RequestUtils.siteId());
    }

    /**
     * 通过外部交易号获取消息跳转路径
     * @param outTradeNo
     * @return
     */
    @Override
    public Object getMsgJumpPath(String outTradeNo) {
        Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", outTradeNo));
        if (pay ==  null) return null;

        WapOrderDetailPathDefiner.WapOrderDetailPathEvent event = new WapOrderDetailPathDefiner.WapOrderDetailPathEvent(pay);
        List<WapOrderDetailPathDefiner.WapOrderDetailPathResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);

        if (ObjectUtil.isNotEmpty(eventData)) {
            return eventData.get(0);
        }
        return null;
    }
}
