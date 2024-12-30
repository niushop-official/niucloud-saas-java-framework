package com.niu.shop.service.core.refund.vo;

import com.niu.shop.entity.order.ShopOrderGoods;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRefundMoneyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal refundMoney;

    private List<ShopOrderGoods> orderGoodsList;

    @Data
    public static class ShopOrderGoods extends com.niu.shop.entity.order.ShopOrderGoods {
        private Integer isRefundDelivery;

        private BigDecimal itemRefundMoney;
    }
}
