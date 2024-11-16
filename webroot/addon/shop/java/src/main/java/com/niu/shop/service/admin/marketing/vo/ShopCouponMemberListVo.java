package com.niu.shop.service.admin.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.shop.enums.coupon.CouponMemberReceiveTypeEnum;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.enums.coupon.CouponReceiveTypeEnum;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private Integer status;  // 状态
    private String title;  // 优惠券名称
    private BigDecimal price;  // 面值
    private BigDecimal minConditionMoney;  // 最低使用门槛
    private String receiveType;  // 领取方式
    private Integer tradeId;  // 关联业务id

    /** 会员编码 */
    private String memberNo;

    /** 会员用户名 */
    private String username;

    /** 手机号 */
    private String mobile;

    /** 会员昵称 */
    private String nickname;

    private MemberBriefInfoVo member;

    /**
     * 获取券领取类型
     * @return
     */
    public String getReceiveTypeName() {
        return CouponMemberReceiveTypeEnum.getReceiveTypeName(this.receiveType);
    }

    /**
     * 获取状态名称
     * @return
     */
    public String getStatusName() {
        return CouponMemberStatusEnum.getNameByStatus(this.status);
    }

}
