package com.niu.shop.service.admin.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.coupon.CouponReceiveTypeEnum;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.enums.coupon.CouponTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopCoupon列表视图
 */
@Data
public class ShopCouponListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 自增ID
    private Integer siteId;  // 站点id
    private String title;  // 名称
    //    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private String startTime; // 活动开启时间
    //    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private String endTime; // 活动结束时间
    private Integer remainCount;  // 剩余数量
    private Integer receiveCount;  // 已领取数量
    private Integer limitCount;  // 单个会员限制领取数量
    private Long receiveUseCount;  // 获取使用数量
    private Integer status;  //  状态 1 正常 2 未开启 3 已无效
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    private BigDecimal price;  // 面值
    private BigDecimal minConditionMoney;  // 商品最低多少金额可用优惠券
    private Integer type;  // 优惠券类型 1通用优惠券 2商品品类优惠券 3商品优惠券
    private Integer receiveType;  // 领取方式
    private Integer validType;  // 有效时间
    private Integer length;  // 有效期时长(天)
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long validStartTime;  // 有效期开始时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long validEndTime;  // 有效期结束时间
    private Integer sort;  // 排序
    private Integer receiveStatus;  //  状态 1 正常 2 关闭

    /**
     * 获取券类型
     *
     * @return
     */
    public String getTypeName() {
        return CouponTypeEnum.getNameByType(this.type);
    }

    /**
     * 获取券领取类型
     *
     * @return
     */
    public String getReceiveTypeName() {
        return CouponReceiveTypeEnum.getNameByType(this.receiveType);
    }

    /**
     * 获取状态名称
     *
     * @return
     */
    public String getStatusName() {
        return CouponStatusEnum.getNameByStatus(this.status);
    }

    /**
     * 获取全部数量
     *
     * @return
     */
    public Integer getSumCount() {
        if (this.remainCount != -1) {
            return this.remainCount + this.receiveCount;
        } else {
            return -1;
        }
    }


}
