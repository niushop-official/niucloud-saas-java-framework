package com.niu.shop.entity.discount;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopDiscountGoods {
    /**
     * 活动商品id
     */
    @TableId(value = "discount_goods_id", type = IdType.AUTO)
    private Integer discountGoodsId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 活动id
     */
    private Integer discountId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品规格id
     */
    private Integer skuId;

    /**
     * 商品状态
     */
    private String status;

    /**
     * 折扣类型
     */
    private String type;

    /**
     * 折扣
     */
    private BigDecimal rate;

    /**
     * 减钱
     */
    private BigDecimal reduceMoney;

    /**
     * 活动商品价格（展示，搜索）
     */
    private BigDecimal discountPrice;

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
     * 是否参与活动
     */
    private Integer isEnabled;

}
