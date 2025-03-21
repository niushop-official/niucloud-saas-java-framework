package com.niu.core.service.core.weapp.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingIsTradeManagedResponse;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import org.springframework.stereotype.Service;

@Service
public class CoreWeappDeliveryServiceImpl implements ICoreWeappDeliveryService {

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
            vo.setIsTradeManaged(res.getTradeManaged());
        } catch (Exception e) {
        }
        return vo;
    }
}
