package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 订单参数
*/
@Data
public class ShopOrderParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "orderId参数缺失")
    private Integer orderId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "orderNo参数缺失")
    private String orderNo;

    @NotNull(message = "body参数缺失")
    private String body;

    @NotNull(message = "orderType参数缺失")
    private String orderType;

    @NotNull(message = "orderFrom参数缺失")
    private String orderFrom;

    @NotNull(message = "outTradeNo参数缺失")
    private String outTradeNo;

    @NotNull(message = "status参数缺失")
    private String status;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "ip参数缺失")
    private String ip;

    @NotNull(message = "goodsMoney参数缺失")
    private BigDecimal goodsMoney;

    @NotNull(message = "deliveryMoney参数缺失")
    private BigDecimal deliveryMoney;

    @NotNull(message = "discountMoney参数缺失")
    private BigDecimal discountMoney;

    @NotNull(message = "orderMoney参数缺失")
    private BigDecimal orderMoney;

    @NotNull(message = "payMoney参数缺失")
    private BigDecimal payMoney;

    @NotNull(message = "invoiceId参数缺失")
    private Integer invoiceId;

    @NotNull(message = "payTime参数缺失")
    private Integer payTime;

    @NotNull(message = "deliveryTime参数缺失")
    private Integer deliveryTime;

    @NotNull(message = "takeTime参数缺失")
    private Integer takeTime;

    @NotNull(message = "finishTime参数缺失")
    private Integer finishTime;

    @NotNull(message = "closeTime参数缺失")
    private Integer closeTime;

    @NotNull(message = "timeout参数缺失")
    private Integer timeout;

    @NotNull(message = "deliveryType参数缺失")
    private String deliveryType;

    @NotNull(message = "takeStoreId参数缺失")
    private Integer takeStoreId;

    @NotNull(message = "takerName参数缺失")
    private String takerName;

    @NotNull(message = "takerMobile参数缺失")
    private String takerMobile;

    @NotNull(message = "takerProvince参数缺失")
    private Integer takerProvince;

    @NotNull(message = "takerCity参数缺失")
    private Integer takerCity;

    @NotNull(message = "takerDistrict参数缺失")
    private Integer takerDistrict;

    @NotNull(message = "takerAddress参数缺失")
    private String takerAddress;

    @NotNull(message = "takerFullAddress参数缺失")
    private String takerFullAddress;

    @NotNull(message = "takerLongitude参数缺失")
    private String takerLongitude;

    @NotNull(message = "takerLatitude参数缺失")
    private String takerLatitude;

    @NotNull(message = "takerStoreId参数缺失")
    private String takerStoreId;

    @NotNull(message = "isEnableRefund参数缺失")
    private Integer isEnableRefund;

    @NotNull(message = "memberRemark参数缺失")
    private String memberRemark;

    @NotNull(message = "shopRemark参数缺失")
    private String shopRemark;

    @NotNull(message = "closeRemark参数缺失")
    private String closeRemark;

    @NotNull(message = "closeType参数缺失")
    private String closeType;

    @NotNull(message = "refundStatus参数缺失")
    private Integer refundStatus;

    @NotNull(message = "hasGoodsTypes参数缺失")
    private String hasGoodsTypes;

    @NotNull(message = "isEvaluate参数缺失")
    private Integer isEvaluate;

    @NotNull(message = "relateId参数缺失")
    private Integer relateId;

    @NotNull(message = "point参数缺失")
    private Integer point;

    @NotNull(message = "activityType参数缺失")
    private String activityType;

}
