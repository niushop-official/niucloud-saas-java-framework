package com.niu.shop.service.admin.order.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.enums.order.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopOrder视图
 */
@Data
public class ShopOrderInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;  // 
    private Integer siteId;  // 站点id
    private String orderNo;  // 订单编号
    private String body;  // 订单内容
    private String orderType;  // 订单类型
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
    private Long closeTime;  // 订单关闭时间
    private Long timeout;  // 通用业务超时时间记录
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
    private List<OrderGoods> orderGoods;
    private Pay pay;
    private Long nowTime = System.currentTimeMillis() / 1000;
    private Long expireTime;
    private Store store;
    private List<OrderDelivery> orderDelivery;
    private MemberBriefInfoVo member;
    private List<ShopOrderLogListVo> orderLog;
    private Integer formRecordId;
    private Boolean formRecordShow;
    private String verifyCode = ""; //核销码
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String verifierMember = ""; //核销员

    // 订单优惠信息
    private List<ShopOrderDiscount> OrderDiscount;

    private String couponMoney = "0.0";

    private String manjianDiscountMoney = "0.0";

    private String buyerAskDeliveryTime = "";

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
        private Integer status; // 状态
        private String statusName;
        private String orderRefundNo; // 退款单号
        private String goodsType; // 商品类型
        private String deliveryStatus; // 配送状态
        private String deliveryStatusName;
        private Integer verifyCount; // 已核销次数
        private Integer verifyExpireTime; // 过期时间 0 为永久
        private Integer isVerify; // 是否需要核销
        private BigDecimal discountMoney;
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String extend;
        private String unit;
        private Integer isGift; // 是否是赠品
        private Integer shopActiveRefund; //商家主动退款0：否 1：是
        private BigDecimal shopActiveRefundMoney; //商家主动退款金额
        private ShopManjian manjianInfo;
        private Integer formRecordId;
        private Boolean formRecordShow;
        private String expressCompanyName;
        private String expressNumber;
        private DeliveryInfo deliveryInfo;
        private Map<String, String> impulseBuyInfo;

        public String getGoodsImageThumbSmall() {
            return CommonUtils.thumbImageSmall(this.siteId, this.getGoodsImage());
        }

        public String getStatusName() {
            if (this.status != 0) {
                return OrderGoodsRefundEnum.getNameByStatus(this.status);
            }
            return "";
        }

        public String getDeliveryStatusName() {
            if (!this.deliveryStatus.isEmpty()) {
                return OrderDeliveryStatusEnum.getNameByStatus(this.deliveryStatus);
            }
            return "";
        }

        public Boolean getFormRecordShow() {
            if (this.formRecordId != null && this.formRecordId > 0) {
                return true;
            }else{
                return false;
            }
        }
    }

    @Data
    public static class DeliveryInfo {
        private Integer deliveryId;
        private String expressCompanyName;
        private String expressNumber;
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

    @Data
    public static class Store {
        private Integer storeId; //
        private String storeName; // 门店名称
        private String storeLogo; // 门店logo
        private String storeMobile; // 手机号
        private String fullAddress; // 完整地址
        private String longitude; // 经度
        private String latitude; // 纬度
        private String tradeTime; // 营业时间
    }

    @Data
    public static class OrderDelivery {
        private Integer id; // id
        private Integer siteId; // 站点id
        private Integer orderId; //
        private String name; // 包裹名称
        private String deliveryType; // 配送方式
        private String subDeliveryType; // 详细配送方式
        private Integer expressCompanyId; // 快递公司id
        private String expressNumber; // 配送单号
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime; // 创建时间
    }

    public String getOrderTypeName() {
        return "";
    }

    public String getOrderFromName() {
        if (!ChannelEnum.getNameByCode(this.orderFrom).isEmpty()) {
            return ChannelEnum.getNameByCode(this.orderFrom);
        }
        return OrderFromTypeEnum.getType().getStr(this.orderFrom, "");
    }

    public String getDeliveryTypeName() {
        return OrderDeliveryTypeEnum.getNameByType(this.deliveryType);
    }

    public Map<String, Object> getStatusName() {
        Map<String, Object> res = OrderStatusEnum.getMapByStatus(Integer.valueOf(this.status));
        if (status.equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString()) && this.deliveryType.equals("store")){
            res.put("name", "待提货");
            return res;
        } else {
            return res;
        }
    }

    public Boolean getFormRecordShow() {
        if (this.formRecordId != null && this.formRecordId > 0) {
            return true;
        }else{
            return false;
        }
    }

}
