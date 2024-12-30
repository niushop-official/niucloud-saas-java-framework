package com.niu.shop.service.core.marketing.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 满减送记录表参数
 */
@Data
public class ShopManjianGiveRecordsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 赠送余额
     */
    private BigDecimal balance;

    /**
     * 赠送优惠券
     */
    private String couponJson;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 赠送商品
     */
    private String goodsJson;

    /**
     * 优惠层级
     */
    private Integer level;

    /**
     * 满减送活动id
     */
    private Integer manjianId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 赠送积分数量
     */
    private Integer point;

    /**
     * 赠送记录id
     */
    private Integer recordId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 满足条件的商品规格id
     */
    private String skuIds;


}
