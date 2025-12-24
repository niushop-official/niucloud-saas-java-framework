package com.niu.shop.service.core.third_addon.order.param;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ActivityOrderCreateParam {
    private Integer id;
    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付流水号
     */
    private String outTradeNo;

    /**
     * 订单内容（商品名称）
     */
    private String body;

    /**
     * 订单状态
     */
    private String status;

    /**
     * ip
     */
    private String ip;

    /**
     * 商品金额
     */
    private BigDecimal goodsMoney;

    /**
     * 订单金额
     */
    private BigDecimal orderMoney;

    /**
     * 支付金额
     */
    private BigDecimal payMoney;

    /**
     * 订单支付时间
     */
    private Long payTime;

    /**
     * 订单关闭时间
     */
    private Long closeTime;

    /**
     * 订单过期时间
     */
    private Long timeout;

    /**
     * 订单来源（H5 PC web）等
     */
    private String orderFrom;

    /**
     * 收货人
     */
    private String takerName;

    /**
     * 收货人手机号
     */
    private String takerMobile;

    /**
     * 收货省
     */
    private Integer takerProvince;

    /**
     * 收货市
     */
    private Integer takerCity;

    /**
     * 收货区县
     */
    private Integer takerDistrict;

    /**
     * 收货地址
     */
    private String takerAddress;

    /**
     * 收货详细地址
     */
    private String takerFullAddress;

    /**
     * 收货地址经度
     */
    private String takerLongitude;

    /**
     * 收货详细纬度
     */
    private String takerLatitude;

    /**
     * 是否允许退款
     */
    private Integer isEnableRefund;

    /**
     * 关闭原因
     */
    private String closeRemark;

    /**
     * 关闭来源(未支付自动关闭   手动关闭  退款关闭)
     */
    private String closeType;

    /**
     * 万能表单记录id
     */
    private Integer formRecordId;

    /**
     * 收货方式
     */
    private String deliveryType;

    /**
     * 用户留言
     */
    private String memberRemark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 是否删除(针对后台)
     */
    private Long deleteTime;

    /**
     * 业务线来源 默认pintuan
     */
    private String source;

    /**
     * 活动id
     */
    private Integer activityId;

    private Integer num;

    /**
     * 运费
     */
    private BigDecimal deliveryMoney;

    private Integer shopGoodsId;

    private Integer shopSkuId;

    private String skuName;

    private String skuImage;

    private BigDecimal originalPrice;

    private String goodsName;

    private String goodsImage;
}
