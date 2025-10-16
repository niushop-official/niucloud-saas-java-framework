package com.niu.shop.service.core.third_addon.order.vo;

import com.niu.core.entity.member.MemberAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDeliveryVo {
    private Integer siteId;
    private Integer memberId;
    private DeliveryData delivery;
    private List<SkuData> skuData;

    @Data
    public static class DeliveryData {
        private String deliveryType;
        private MemberAddress takeAddress;
    }

    @Data
    public static class SkuData {
        private Integer skuId;
        private Integer num;
        private String marketType;
        private Integer marketTypeId;
    }

    @Data
    public static class BasicData {
        private List<String> hasGoodsTypes;
        private Integer totalNum;
        private BigDecimal goodsMoney;
        private BigDecimal deliveryMoney;
        private String body;
    }


}
