package com.niu.core.service.core.weapp.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class CoreWeappDeliveryServiceImpl implements ICoreWeappDeliveryService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @param siteId
     * @return
     */
    @Override
    public IsTradeManagedVo getIsTradeManaged(Integer siteId) {
        IsTradeManagedVo vo = new IsTradeManagedVo();
        try {
            WxMaService miniapp = WechatUtils.miniapp(siteId);
            String appid = miniapp.getWxMaConfig().getAppid();
            WxMaOrderShippingIsTradeManagedResponse res = miniapp.getWxMaOrderShippingService().isTradeManaged(appid);
            if (!res.getTradeManaged()) {
                System.out.println("小程序未开通发货信息管理服务" + res.getErrMsg());
            }
            vo.setIsTradeManaged(res.getTradeManaged());
        } catch (Exception e) {
        }
        return vo;
    }

    @Override
    public WxMaOrderShippingInfoBaseResponse setMsgJumpPath(Integer siteId, String type) {
        try {
            JSONObject config = getConfig(siteId, type);
            if (ObjectUtils.isEmpty(config)) {
                String path = "app/pages/weapp/order_shipping";
                WxMaOrderShippingInfoBaseResponse response = WechatUtils.miniapp(siteId).getWxMaOrderShippingService().setMsgJumpPath(path);
                if (response.getErrCode() == 0) {
                    setConfig(siteId, type, path);
                }
                return response;
            }

            WxMaOrderShippingInfoBaseResponse response = new WxMaOrderShippingInfoBaseResponse();
            response.setErrCode(0);
            return response;
        } catch (Exception e) {
            WxMaOrderShippingInfoBaseResponse response = new WxMaOrderShippingInfoBaseResponse();
            response.setErrCode(1);
            response.setErrMsg(e.getMessage());
            return response;
        }
    }

    public JSONObject getConfig(Integer siteId, String type) {
        return coreConfigService.getConfigValue(siteId, "WEAPP_ORDER_SHIPPING_CONFIG_" + type);
    }

    public void setConfig(Integer siteId, String type, String path) {
        JSONObject value = new JSONObject();
        value.put("path", path);
        coreConfigService.setConfig(siteId, "WEAPP_ORDER_SHIPPING_CONFIG_" + type, value);
    }

}
