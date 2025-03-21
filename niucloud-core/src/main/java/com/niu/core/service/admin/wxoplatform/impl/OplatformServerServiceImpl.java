package com.niu.core.service.admin.wxoplatform.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.weapp.WeappVersion;
import com.niu.core.enums.channel.WeappVersionStatusEnum;
import com.niu.core.mapper.weapp.WeappVersionMapper;
import com.niu.core.service.admin.wxoplatform.IOplatformServerService;
import com.niu.core.service.admin.wxoplatform.IOplatformService;
import com.niu.core.service.admin.wxoplatform.param.AuthorizationParam;
import com.niu.core.service.admin.wxoplatform.param.OplatformServerParam;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wxoplatform.ICoreOplatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.open.api.WxOpenMaService;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OplatformServerServiceImpl implements IOplatformServerService {

    private final IOplatformService oplatformService;
    private final ICoreOplatformService coreOplatformService;
    private final WeappVersionMapper weappVersionMapper;
    private final ICoreWeappConfigService coreWeappConfigService;

    /**
     * 处理开发平台授权推送消息
     * @param param
     */
    @Override
    public void server(OplatformServerParam param) {
        if (!StringUtils.equalsIgnoreCase("aes", param.getEncryptType())
            || !WechatUtils.WxOpen().getWxOpenComponentService().checkSignature(param.getTimestamp(), param.getNonce(), param.getSignature())) {
            throw new IllegalArgumentException("非法请求");
        }

        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(param.getRequestBody(), WechatUtils.WxOpen().getWxOpenConfigStorage(), param.getTimestamp(), param.getNonce(), param.getMsgSignature());
        log.info("开放平台授权事件推送消息：{}", inMessage);

        try {
            WechatUtils.WxOpen().getWxOpenComponentService().route(inMessage);

            // 授权取消
            if (inMessage.getInfoType().equals("unauthorized")) {
                oplatformService.clearAuthorization(inMessage.getAuthorizerAppid());
            }
            // 更新授权
            if (inMessage.getInfoType().equals("updateauthorized")) {
                Integer siteId = coreOplatformService.getSiteIdByAuthorizerAppid(inMessage.getAuthorizerAppid());
                RequestUtils.setSiteId(siteId);

                AuthorizationParam authorizationParam = new AuthorizationParam();
                authorizationParam.setAuthCode(inMessage.getAuthorizationCode());
                oplatformService.authorization(authorizationParam);
            }
        } catch (Exception e) {
            log.error("处理开放平台授权事件消息异常", e);
        }
    }

    /**
     * 处理消息推送
     * @param param
     */
    public String message(String appid, OplatformServerParam param) {
        if (!StringUtils.equalsIgnoreCase("aes", param.getEncryptType())
            || !WechatUtils.WxOpen().getWxOpenComponentService().checkSignature(param.getTimestamp(), param.getNonce(), param.getSignature())) {
            throw new IllegalArgumentException("非法请求");
        }

        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(param.getRequestBody(), WechatUtils.WxOpen().getWxOpenConfigStorage(), param.getTimestamp(), param.getNonce(), param.getMsgSignature());
        log.info("开放平台消息与事件推送消息：{}", inMessage);

       if (inMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
           // 小程序审核成功
           if (inMessage.getEvent().equals(WxConsts.EventType.WEAPP_AUDIT_SUCCESS)) {
               weappAuditSuccess(inMessage);
           }
           // 小程序审核成功
           if (inMessage.getEvent().equals(WxConsts.EventType.WEAPP_AUDIT_SUCCESS)) {
                weappAuditFail(inMessage);
           }
       }

        WxOpenMessageRouter wxOpenMessageRouter = new WxOpenMessageRouter(WechatUtils.WxOpen());

        WxMpXmlOutMessage outMessage = wxOpenMessageRouter.route(inMessage, appid);
        if(outMessage != null){
            return WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(outMessage, WechatUtils.WxOpen().getWxOpenConfigStorage());
        }
        return "success";
    }

    /**
     * 小程序审核成功
     * @param inMessage
     */
    private void weappAuditSuccess(WxMpXmlMessage inMessage) {
        Integer siteId = coreOplatformService.getSiteIdByAuthorizerAppid(inMessage.getToUser());

        WeappVersion version = weappVersionMapper.selectOne(new QueryWrapper<WeappVersion>()
            .eq("site_id", siteId)
            .eq("status", WeappVersionStatusEnum.APPLET_AUDITING.getStatus())
        );

        if (ObjectUtil.isNotNull(version)) {
            WeappConfigVo weappCofig = coreWeappConfigService.getWeappConfig(siteId);

            WxOpenMaService wxOpenMaService = WechatUtils.WxOpen().getWxOpenComponentService()
                .getWxMaServiceByAppid(weappCofig.getAppId());

            try {
                wxOpenMaService.releaseAudited();
            } catch (Exception e) {
            }
            version.setStatus(WeappVersionStatusEnum.APPLET_PUBLISHED.getStatus());
            weappVersionMapper.updateById(version);
        }
    }

    /**
     * 小程序审核失败
     * @param inMessage
     */
    private void weappAuditFail(WxMpXmlMessage inMessage) {
        Integer siteId = coreOplatformService.getSiteIdByAuthorizerAppid(inMessage.getToUser());

        WeappVersion version = weappVersionMapper.selectOne(new QueryWrapper<WeappVersion>()
            .eq("site_id", siteId)
            .eq("status", WeappVersionStatusEnum.APPLET_AUDITING.getStatus())
        );

        if (ObjectUtil.isNotNull(version)) {
            version.setStatus(WeappVersionStatusEnum.APPLET_AUDIT_FAIL.getStatus());
            version.setFailReason(inMessage.getReason());
            weappVersionMapper.updateById(version);
        }
    }
}
