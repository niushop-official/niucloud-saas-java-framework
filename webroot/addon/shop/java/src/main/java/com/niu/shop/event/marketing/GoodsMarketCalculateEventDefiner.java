package com.niu.shop.event.marketing;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 活动信息
 */
public abstract class GoodsMarketCalculateEventDefiner extends CallbackListener<GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent> {

    @Data
    public static class GoodsMarketCalculateEvent extends Event {
        private OrderCreateDataVo vo;
        private OrderCreateDataVo.Sku skuInfo;
        private List<OrderCalculateParam.SkuItem> skuData;
    }

    @Data
    public static class GoodsMarketCalculateResult extends EventResult {
        private OrderCreateDataVo.Sku skuInfo;
        private Integer relateId;
        private String activeType;
        private Map<String, BigDecimal> basic;
    }

    public abstract GoodsMarketCalculateResult handleCallback(GoodsMarketCalculateEvent event);
}
