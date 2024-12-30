package com.niu.shop.service.api.cart.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopCart列表视图
 */
@Data
public class ShopCartCalculateResVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal goodsMoney;

    private BigDecimal promotionMoney;

    private BigDecimal orderMoney;

    private List<Goods> matchList;




    @Data
    public static class Goods {

        private Integer skuId; // 商品sku_id
        private Integer siteId; // 站点id
        private String skuName; // 商品sku名称
        private BigDecimal price; // sku单价
        private Integer stock; // 商品sku库存
        private Integer goodsId; // 商品id
        private Integer cartId;
        private Integer num;
        private BigDecimal memberPrice;
        private String memberPriceDesc;
        private String memberDiscount;
        private BigDecimal goodsMoney;
        private BigDecimal realGoodsMoney;
        private BigDecimal couponMoney;
        private BigDecimal promotionMoney;
        private Map<String, Object> promotion;
        private Integer level;
        private Integer hasBuy;

    }
}
