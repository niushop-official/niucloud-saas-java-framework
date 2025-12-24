package com.niu.shop.service.api.marketing.vo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    private Integer activeGoodsId;  // 活动id
    private Integer discountId;  // 活动id
    private Integer activeId;  // 活动id
    private Integer siteId;  // 站点id
    private Integer goodsId;  // 商品id
    private String type;  // 商品活动类型（单品，独立商品，店铺整体商品）
    private String status;  // 活动状态
    private Discount active;
    private BigDecimal discountPrice;
    private Long startTime; // 活动开始时间
    private Long endTime; // 活动结束时间

    @Data
    public static class Discount{


        private Integer activeId;

        private Integer discountId;

        /**
         * 站点id
         */
        private Integer siteId;

        /**
         * 活动名称
         */
        private String activeName;

        /**
         * 活动说明
         */
        private String activeDesc;

        /**
         * 活动开始时间
         */
        private Long startTime;

        /**
         * 活动结束时间
         */
        private Long endTime;

        /**
         * 活动状态
         */
        private String status;

        /**
         * 活动累计金额
         */
        private BigDecimal orderMoney;

        /**
         * 活动累计订单数
         */
        private Integer orderNum;

        /**
         * 活动参与会员数
         */
        private Integer memberNum;

        /**
         * 活动成功参与会员数
         */
        private Integer successNum;

        /**
         * 添加时间
         */
        private Long createTime;

        /**
         * 修改时间
         */
        private Long updateTime;
    }

    public Long getStartTime() {
        return this.active.getStartTime();
    }

    public Long getEndTime() {
        return this.active.getEndTime();
    }
}
