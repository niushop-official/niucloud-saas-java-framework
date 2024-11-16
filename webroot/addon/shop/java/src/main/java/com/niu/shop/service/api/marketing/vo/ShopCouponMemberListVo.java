package com.niu.shop.service.api.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.coupon.CouponReceiveTypeEnum;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.enums.coupon.CouponTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopCouponMember列表视图
 */
@Data
public class ShopCouponMemberListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 优惠券发放记录id
    private Integer siteId;  // 站点id
    private Integer couponId;  // 优惠券id
    private Integer memberId;  // 会员id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 领取时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime;  // 过期时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long useTime;  // 使用时间
    private String type;  // 优惠券类型
    private String typeName;
    private Integer status;  // 状态
    private String statusName;
    private String title;  // 优惠券名称
    private BigDecimal price;  // 面值
    private BigDecimal minConditionMoney;  // 最低使用门槛
    private Integer receiveType;  // 领取方式
    private String receiveTypeName;
    private Integer tradeId;  // 关联业务id

    /**
     * 获取券领取类型
     * @return
     */
    public String getReceiveTypeName() {
        return CouponReceiveTypeEnum.getNameByType(this.receiveType);
    }

    /**
     * 获取状态名称
     * @return
     */
    public String getStatusName() {
        return CouponStatusEnum.getNameByStatus(this.status);
    }

    /**
     * 获取优惠券价格，去除多余的0
     * @return
     */
    public BigDecimal getCouponPrice(){
        return this.price.stripTrailingZeros();
    }

    /**
     * 获取优惠券最低使用价格
     * @return
     */
    public BigDecimal getCouponMinPrice(){
        return this.minConditionMoney.stripTrailingZeros();
    }

    /**
     * 获取类型名称
     * @return
     */
    public String getTypeName(){
        return CouponTypeEnum.getNameByType(Integer.parseInt(this.type));
    }


}
