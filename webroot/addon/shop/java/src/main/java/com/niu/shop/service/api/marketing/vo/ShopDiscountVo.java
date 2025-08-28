package com.niu.shop.service.api.marketing.vo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.coupon.CouponReceiveTypeEnum;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.enums.coupon.CouponTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * ShopCoupon视图
 */
@Data
public class ShopDiscountVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 活动id
     */
    private Integer discountId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动说明
     */
    private String remark;

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

    private String statusName;

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


    public String getStatusName() {
        return ActiveStatusEnum.getNameByStatus(this.status);
    }
}
