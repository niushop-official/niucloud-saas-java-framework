package com.niu.core.common.utils;

import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.niu.core.component.context.SpringContext;
import com.niu.core.enums.channel.WechatEncryptionTypeEnum;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * 微信接口工具类
 */

public class WechatUtils {

    /**
     * 微信公众号接口
     *
     * @param siteId
     * @return
     */
    public static WxMpService mp(Integer siteId) {
        ICoreWechatConfigService coreWechatConfigService = (ICoreWechatConfigService) SpringContext.getBean(ICoreWechatConfigService.class);

        WxMpService service = new WxMpServiceImpl();
        WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
        WechatConfigVo wechatConfigVo = coreWechatConfigService.getWechatConfig(siteId);
        wxMpDefaultConfig.setAppId(wechatConfigVo.getAppId());
        wxMpDefaultConfig.setSecret(wechatConfigVo.getAppSecret());
        wxMpDefaultConfig.setToken(wechatConfigVo.getToken());
        if (!wechatConfigVo.getEncryptionType().equals(WechatEncryptionTypeEnum.NOT_ENCRYPT.getType())) wxMpDefaultConfig.setAesKey(wechatConfigVo.getEncodingAesKey());
        //初始化配置
        service.setWxMpConfigStorage(wxMpDefaultConfig);
        return service;
    }

    /**
     * 微信小程序接口
     *
     * @param siteId
     * @return
     */
    public static WxMaServiceImpl miniapp(Integer siteId) {
        ICoreWeappConfigService coreWeappConfigService = (ICoreWeappConfigService) SpringContext.getBean(ICoreWeappConfigService.class);

        WxMaServiceImpl service = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();

        WeappConfigVo weappConfigVo = coreWeappConfigService.getWeappConfig(siteId);
        wxMaDefaultConfig.setAppid(weappConfigVo.getAppId());
        wxMaDefaultConfig.setSecret(weappConfigVo.getAppSecret());
        wxMaDefaultConfig.setToken(weappConfigVo.getToken());
        wxMaDefaultConfig.setAesKey(weappConfigVo.getEncodingAesKey());

        return service;
    }

}
