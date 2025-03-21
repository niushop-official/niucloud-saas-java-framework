package com.niu.shop.service.core.notice.driver;

import cn.hutool.core.date.DateUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.shop.entity.order.ShopOrder;

import java.util.HashMap;
import java.util.Map;

public class ShopOrderDelivery extends BaseNotice {

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        ShopOrder order = (ShopOrder) vars.get("order");

        ICoreSysConfigService coreSysConfigService = SpringContext.bean(ICoreSysConfigService.class);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("body", StringUtils.substring(order.getBody(), 0, 10) + "...");
        param.put("order_no", order.getOrderNo());
        param.put("order_money", order.getOrderMoney());
        param.put("delivery_time", DateUtil.date(order.getDeliveryTime() * 1000));
        param.put("wechat_page", "/addon/shop/pages/order/detail?order_id=" + order.getOrderId());
        param.put("weapp_page", "addon/shop/pages/order/detail?order_id=" + order.getOrderId());
        param.put("url", coreSysConfigService.getSceneDomain(order.getSiteId()).getWapUrl() + "/addon/shop/pages/order/detail?order_id=" + order.getOrderId());

        NoticeDataVo vo = new NoticeDataVo();
        vo.setMemberId(order.getMemberId());
        vo.setVars(param);

        return vo;
    }
}
