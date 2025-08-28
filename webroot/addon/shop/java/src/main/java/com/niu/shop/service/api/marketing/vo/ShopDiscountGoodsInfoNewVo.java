package com.niu.shop.service.api.marketing.vo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.niu.shop.entity.active.ShopActive;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopActive视图
 */
@Data
public class ShopDiscountGoodsInfoNewVo implements Serializable {

    /**
     * 活动商品id
     */
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

    public Long getStartTime() {
        return this.getStartTime();
    }

    public Long getEndTime() {
        return this.getEndTime();
    }
}
