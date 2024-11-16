package com.niu.shop.service.core.pointexchange.vo;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class OrderCreateDataVo extends com.niu.shop.service.core.order.vo.OrderCreateDataVo implements Serializable {
    protected static final long serialVersionUID = 1L;

    private Basic basic = new Basic();

    public Map<Integer, Sku> getGoodsData() {
        // 强制类型转换
        return (Map<Integer, Sku>) (Map<Integer, ?>) super.getGoodsData();
    }

    @Data
    public static class Basic extends com.niu.shop.service.core.order.vo.OrderCreateDataVo.Basic {
        private Integer pointSum = 0;
    }

    @Data
    public static class Sku extends com.niu.shop.service.core.order.vo.OrderCreateDataVo.Sku {
        private Integer exchangeId;
        private ExchangeInfo exchangeInfo;
        private Integer pointSum = 0;
    }

    @Data
    public static class ExchangeInfo {
        private Integer goodsId;
        private Integer skuId;
        private Integer point;
        private Integer stock;
        private Integer limitNum;
        private Integer isEnabled;
        private BigDecimal price;
    }

//    public Map<Integer, Sku> getGoodsData() {
//        // 强制类型转换
//        return (Map<Integer, Sku>) (Map<Integer, ?>) super.getGoodsData();
//    }
}
