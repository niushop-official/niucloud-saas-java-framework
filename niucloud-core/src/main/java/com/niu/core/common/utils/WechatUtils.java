package com.niu.core.common.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.channel.WechatEncryptionTypeEnum;
import com.niu.core.service.admin.wxoplatform.vo.OplatformConfigVo;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import com.niu.core.service.core.wxoplatform.ICoreOplatformConfigService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.open.api.WxOpenConfigStorage;
import me.chanjar.weixin.open.api.WxOpenMaService;
import me.chanjar.weixin.open.api.WxOpenMpService;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;

/**
 * 微信接口工具类
 */

public class WechatUtils {

    private static WxOpenServiceImpl wxOpenService;

    /**
     * 微信公众号接口
     *
     * @param siteId
     * @return
     */
    public static WxMpService mp(Integer siteId) {
        ICoreWechatConfigService coreWechatConfigService = (ICoreWechatConfigService) SpringContext.getBean(ICoreWechatConfigService.class);

        WechatConfigVo wechatConfigVo = coreWechatConfigService.getWechatConfig(siteId);

        if (wechatConfigVo.getIsAuthorization().equals(1)) {
            if (wxOpenService == null) {
                WxOpen();
            }
            WxOpenAuthorizerInfoResult authorizationInfo = coreWechatConfigService.getWechatAuthorizationInfo(siteId);

            WxOpenConfigStorage configStorage = wxOpenService.getWxOpenComponentService().getWxOpenConfigStorage();
            configStorage.setAuthorizerRefreshToken(wechatConfigVo.getAppId(), authorizationInfo.getAuthorizationInfo().getAuthorizerRefreshToken());

            // 判断authorizer_access_token 是否为空和过期 刷新token
            if (ObjectUtil.isEmpty(configStorage.getAuthorizerAccessToken(wechatConfigVo.getAppId())) || configStorage.isAuthorizerAccessTokenExpired(wechatConfigVo.getAppId())) {
                try {
                    wxOpenService.getWxOpenComponentService().getAuthorizerAccessToken(wechatConfigVo.getAppId(), true);
                    // 存储新的 authorizer_refresh_token
                    authorizationInfo.getAuthorizationInfo().setAuthorizerRefreshToken(configStorage.getAuthorizerRefreshToken(wechatConfigVo.getAppId()));
                    coreWechatConfigService.setWechatAuthorizationInfo(siteId, authorizationInfo);
                } catch (Exception e) {
                    throw new CommonException(e.getMessage());
                }
            }
            WxOpenMpService service = wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(wechatConfigVo.getAppId());
            return service;
        } else {
            if (wechatConfigVo.getAppId().isEmpty() || wechatConfigVo.getAppSecret().isEmpty()) throw new CommonException("微信公众号未配置完善");

            WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
            wxMpDefaultConfig.setAppId(wechatConfigVo.getAppId());
            wxMpDefaultConfig.setSecret(wechatConfigVo.getAppSecret());
            wxMpDefaultConfig.setToken(wechatConfigVo.getToken());
            if (!wechatConfigVo.getEncryptionType().equals(WechatEncryptionTypeEnum.NOT_ENCRYPT.getType())) wxMpDefaultConfig.setAesKey(wechatConfigVo.getEncodingAesKey());

            WxMpService service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(wxMpDefaultConfig);
            return service;
        }
    }

    /**
     * 微信小程序接口
     *
     * @param siteId
     * @return
     */
    public static WxMaService miniapp(Integer siteId) {
        ICoreWeappConfigService coreWeappConfigService = (ICoreWeappConfigService) SpringContext.getBean(ICoreWeappConfigService.class);

        WeappConfigVo weappConfigVo = coreWeappConfigService.getWeappConfig(siteId);

        // 如果已授权给三方平台
        if (weappConfigVo.getIsAuthorization().equals(1)) {
            if (wxOpenService == null) {
                WxOpen();
            }
            WxOpenAuthorizerInfoResult authorizationInfo = coreWeappConfigService.getWeappAuthorizationInfo(siteId);
            WxOpenConfigStorage configStorage = wxOpenService.getWxOpenComponentService().getWxOpenConfigStorage();
            configStorage.setAuthorizerRefreshToken(weappConfigVo.getAppId(), authorizationInfo.getAuthorizationInfo().getAuthorizerRefreshToken());

            // 判断authorizer_access_token 是否为空和过期 刷新token
            if (ObjectUtil.isEmpty(configStorage.getAuthorizerAccessToken(weappConfigVo.getAppId())) || configStorage.isAuthorizerAccessTokenExpired(weappConfigVo.getAppId())) {
                try {
                    wxOpenService.getWxOpenComponentService().getAuthorizerAccessToken(weappConfigVo.getAppId(), true);
                    // 存储新的 authorizer_refresh_token
                    authorizationInfo.getAuthorizationInfo().setAuthorizerRefreshToken(configStorage.getAuthorizerRefreshToken(weappConfigVo.getAppId()));
                    coreWeappConfigService.setWeappAuthorizationInfo(siteId, authorizationInfo);
                } catch (Exception e) {
                    throw new CommonException(e.getMessage());
                }
            }
            WxOpenMaService service = wxOpenService.getWxOpenComponentService().getWxMaServiceByAppid(weappConfigVo.getAppId());
            return service;
        } else {
            if (weappConfigVo.getAppId().isEmpty() || weappConfigVo.getAppSecret().isEmpty()) throw new CommonException("微信小程序未配置完善");

            WxMaServiceImpl service = new WxMaServiceImpl();

            WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
            wxMaDefaultConfig.setAppid(weappConfigVo.getAppId());
            wxMaDefaultConfig.setSecret(weappConfigVo.getAppSecret());
            wxMaDefaultConfig.setToken(weappConfigVo.getToken());
            wxMaDefaultConfig.setAesKey(weappConfigVo.getEncodingAesKey());
            service.setWxMaConfig(wxMaDefaultConfig);

            return service;
        }
    }

    /**
     * 微信开放平台接口
     * @return
     */
    public static WxOpenServiceImpl WxOpen() {
        if (wxOpenService == null) {
            ICoreOplatformConfigService configService = (ICoreOplatformConfigService) SpringContext.getBean(ICoreOplatformConfigService.class);
            OplatformConfigVo oplatformConfigVo = configService.getWxOplatformConfig();
            if (oplatformConfigVo.getAppId().isEmpty() || oplatformConfigVo.getAppSecret().isEmpty()) throw new CommonException("未配置微信开放平台");

            WxOpenInMemoryConfigStorage configStorage = new WxOpenInMemoryConfigStorage();
            configStorage.setComponentAppId(oplatformConfigVo.getAppId());
            configStorage.setComponentAppSecret(oplatformConfigVo.getAppSecret());
            configStorage.setComponentToken(oplatformConfigVo.getToken());
            configStorage.setComponentAesKey(oplatformConfigVo.getAesKey());

            WxOpenServiceImpl service = new WxOpenServiceImpl();
            service.setWxOpenConfigStorage(configStorage);
            wxOpenService = service;

            return service;
        }

        return wxOpenService;
    }

}
