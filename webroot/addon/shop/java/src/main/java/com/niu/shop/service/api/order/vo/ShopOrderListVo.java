package com.niu.shop.service.api.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopOrder列表视图
 */
@Data
public class ShopOrderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;  // 
    private Integer siteId;  // 站点id
    private String orderNo;  // 订单编号
    private String body;  // 订单内容
    private String orderType;
    private String orderTypeName;// 订单类型
    private String orderFrom;  // 订单来源
    private String orderFromName;
    private String outTradeNo;  // 支付流水号
    private String status;  // 订单状态
    private Map<String, Object> statusName;
    private Integer memberId;  // 会员id
    private String ip;  // ip
    private BigDecimal goodsMoney;  // 商品金额
    private BigDecimal deliveryMoney;  // 配送金额
    private BigDecimal discountMoney;  // 优惠金额
    private BigDecimal orderMoney;  // 订单金额
    private BigDecimal payMoney;  // 支付金额
    private Integer invoiceId;  // 发票id，0表示不开发票
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;  // 订单支付时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long deliveryTime;  // 订单发货时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long takeTime;  // 订单收货时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long finishTime;  // 订单完成时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Integer closeTime;  // 订单关闭时间
    private Integer timeout;  // 通用业务超时时间记录
    private String deliveryType;  // 配送方式
    private String deliveryTypeName;
    private Integer takeStoreId;  // 自提点
    private String takerName;  // 收货人
    private String takerMobile;  // 收货人手机号
    private Integer takerProvince;  // 收货省
    private Integer takerCity;  // 收货市
    private Integer takerDistrict;  // 收货区县
    private String takerAddress;  // 收货地址
    private String takerFullAddress;  // 收货详细地址
    private String takerLongitude;  // 收货地址经度
    private String takerLatitude;  // 收货详细纬度
    private String takerStoreId;  // 收货门店
    private Integer isEnableRefund;  // 是否允许退款
    private String memberRemark;  // 会员留言信息
    private String shopRemark;  // 商家留言
    private String closeRemark;  // 关闭原因
    private String closeType;  // 关闭来源(未支付自动关闭   手动关闭  退款关闭)
    private Integer refundStatus;  // 退款状态  1不存在退款  2 部分退款  3 全部退款
    private String hasGoodsTypes;  // 包含的商品类型 json
    private Integer isEvaluate;  // 是否评论
    private Integer relateId;  // 关联id
    private Integer point;  // 积分兑换
    private String activityType;  // 营销类型
    private Long nowTime = System.currentTimeMillis() / 1000;
    private Long expireTime;
    private Pay pay;

    private List<OrderGoods> orderGoods;

    @Data
    public static class OrderGoods {
        private Integer orderGoodsId; //
        private Integer siteId; // 站点id
        private Integer orderId; // 订单id
        private Integer memberId; // 购买会员id
        private Integer goodsId; // 商品id
        private Integer skuId; // 商品规格id
        private String goodsName; // 商品名称
        private String skuName; // 商品规格名称
        private String goodsImage; // 商品图片
        private String goodsImageThumbSmall;
        private String skuImage; // sku规格图片
        private BigDecimal price; // 商品单价
        private Integer num; // 购买数量
        private BigDecimal goodsMoney; // 商品总价
        private Integer isEnableRefund; // 是否允许退款
        private Integer deliveryId;

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsImageThumbSmall() {
            return coreUploadService().thumb(this.siteId, this.getGoodsImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        }
    }

    @Data
    public static class Pay {
        private String type;
        private String typeName;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long payTime;

        public String getTypeName() {
            return PayTypeEnum.getType().getByPath(this.type + ".name", String.class);
        }
    }

    public String getOrderTypeName() {
        return "";
    }

    public String getOrderFromName() {
        return ChannelEnum.getNameByCode(this.orderFrom);
    }

    public String getDeliveryTypeName() {
        return OrderDeliveryTypeEnum.getNameByType(this.deliveryType);
    }

    public Map<String, Object> getStatusName() {
        return OrderStatusEnum.getMapByStatus(Integer.valueOf(this.status));
    }
}
