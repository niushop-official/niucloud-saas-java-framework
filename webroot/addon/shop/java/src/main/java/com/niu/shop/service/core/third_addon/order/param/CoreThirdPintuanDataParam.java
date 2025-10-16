package com.niu.shop.service.core.third_addon.order.param;

import cn.hutool.json.JSONObject;
import com.niu.core.entity.pay.Pay;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoreThirdPintuanDataParam {
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
     * 活动ID
     */
    private Integer pintuanId;

    /**
     * 拼团组id
     */
    private Integer groupId;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * sku_id
     */
    private Integer skuId;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 订单内容（商品名称）
     */
    private String body;

    /**
     * 拼团信息快照
     */
    private String pintuanSnapshot;

    /**
     * 商品信息快照（SKU维度）
     */
    private String skuSnapshot;

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
     * 三方附加费用
     */
    private String thirdCostList;

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
     * 退款状态  wait 待退款 refundind退款中 refund已退款
     */
    private String refundStatus;

    /**
     * 万能表单记录id
     */
    private Integer formRecordId;

    /**
     * 业务线来源 默认pintuan
     */
    private String source;

    /**
     * 来源订单id
     */
    private Integer sourceId;

    /**
     * 三方系统订单商品关联表id例如商城系统的order_goods_id
     */
    private Integer sourceOrderItemId;

    /**
     * 收货方式
     */
    private String deliveryType;

    /**
     * 收货方式配置
     */
    private String deliveryTypeConfig;

    /**
     * 扣减库存节点类型
     */
    private String decStockType;

    /**
     * 核销过期时间 需要做定时任务故单提了字段
     */
    private Long verifyExpireTime;

    /**
     * 退款类型
     */
    private String refundType;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 用户留言
     */
    private String memberRemark;

    /**
     * 是否单独购买0-否1-是
     */
    private Integer isSingleBuy;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 编辑时间
     */
    private Long updateTime;

    /**
     * 是否删除(针对后台)
     */
    private Long deleteTime;

    private JSONObject sku;

    private Pay pay;

    private FailRecords failRecords;

    @Data
    public static class FailRecords {
        private Integer id;

        /**
         * 站点id
         */
        private Integer siteId;

        /**
         * 订单id
         */
        private Integer orderId;

        /**
         * 可重试次数 默认3次
         */
        private Integer retryNum;

        /**
         * 失败原因
         */
        private String failReason;

        /**
         * 创建时间（UNIX时间戳）
         */
        private Long createTime;

        /**
         * 最新重试时间（UNIX时间戳）
         */
        private Long retryTime;
    }
}
