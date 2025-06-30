package com.niu.core.service.core.weapp;

import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;

public interface ICoreWeappDeliveryService {

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @param siteId
     * @return
     */
    IsTradeManagedVo getIsTradeManaged(Integer siteId);

    WxMaOrderShippingInfoBaseResponse setMsgJumpPath(Integer siteId, String type);
}
