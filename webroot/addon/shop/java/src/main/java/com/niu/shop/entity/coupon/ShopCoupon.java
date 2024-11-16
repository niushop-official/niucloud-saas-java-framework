package com.niu.shop.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 优惠券实体
 */
@Data
public class ShopCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 自增ID
    private Integer siteId; // 站点id
    private String title; // 名称
    private Long startTime; // 活动开启时间
    private Long endTime; // 活动结束时间
    private Integer remainCount; // 剩余数量
    private Integer receiveCount; // 已领取数量
    private Integer limitCount; // 单个会员限制领取数量
    private Integer status; //  状态 1 正常 2 未开启 3 已无效
    private Long createTime; // 添加时间
    private BigDecimal price; // 面值
    private BigDecimal minConditionMoney; // 商品最低多少金额可用优惠券
    private Integer type; // 优惠券类型 1通用优惠券 2商品品类优惠券 3商品优惠券
    private Integer receiveType; // 领取方式
    private Integer validType; // 有效时间
    private Integer length; // 有效期时长(天)
    private Long validStartTime; // 有效期开始时间
    private Long validEndTime; // 有效期结束时间
    private Integer sort; // 排序
    private Integer receiveStatus; //  状态 1 正常 2 关闭

}