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
public class ShopDiscount {
    /**
     * 活动id
     */
    @TableId(value = "discount_id", type = IdType.AUTO)
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
