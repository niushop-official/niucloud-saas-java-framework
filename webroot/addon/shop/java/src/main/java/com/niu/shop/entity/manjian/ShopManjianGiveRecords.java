package com.niu.shop.entity.manjian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 满减送记录表实体
 */
@Data
public class ShopManjianGiveRecords implements Serializable {

    @Serial
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
    @TableId(value = "record_id", type = IdType.AUTO)
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