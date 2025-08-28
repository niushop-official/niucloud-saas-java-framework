package com.niu.shop.service.admin.marketing.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.StatusEnum;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.enums.coupon.CouponSendStatus;
import com.niu.shop.enums.coupon.SendCouponRangeTypeEnum;
import lombok.Data;

@Data
public class ShopCouponSendRecordsVo {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 优惠券id
     */
    private Integer couponId;

    /*
     * 优惠券信息
     */
    private ShopCoupon coupon;

    /**
     * 每位会员发放数量
     */
    private Integer sendNum;

    /**
     * 发券范围
     */
    private String rangeType;

    private String rangeTypeName;

    /**
     * 范围对应参数
     */
    private String rangeParam;

    private String rangeParamName;

    /**
     * 发放成功数
     */
    private Integer successNum;

    /**
     * 状态 wait-待发送 process-发送中 finish-结束
     */
    private String status;

    private String statusName;

    /**
     * 发放会员数
     */
    private Integer memberNum;

    /**
     * 发放结束时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime;

    /**
     * 操作人id
     */
    private Integer adminUid;

    /**
     * 操作人名称
     */
    private String adminUsername;

    /**
     * 创建时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    public String getStatusName() {
        return CouponSendStatus.getNameByCode(this.status);
    }

    public String getRangeTypeName() {
        return SendCouponRangeTypeEnum.getNameByStatus(this.rangeType);
    }
}
