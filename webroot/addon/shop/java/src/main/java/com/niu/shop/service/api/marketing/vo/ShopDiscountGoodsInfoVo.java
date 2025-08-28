package com.niu.shop.service.api.marketing.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.discount.ShopDiscount;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopActive视图
 */
@Data
public class ShopDiscountGoodsInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer discountGoodsId;  // 活动商品id
    private Integer discountId;  // 活动id
    private Integer siteId;  // 站点id
    private Integer goodsId;  // 商品id
    private String type;  // 商品活动类型（单品，独立商品，店铺整体商品）
    private String status;  // 活动状态
    private ShopDiscount active;
    private BigDecimal discountPrice;
    private Long startTime; // 活动开始时间
    private Long endTime; // 活动结束时间

    public Long getStartTime() {
        return this.active.getStartTime();
    }

    public Long getEndTime() {
        return this.active.getEndTime();
    }
}
