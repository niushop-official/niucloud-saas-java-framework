package com.niu.shop.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 积分兑换订单参数
*/
@Data
public class ShopPointExchangeOrderParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "orderId参数缺失")
    private Integer orderId;

    @NotNull(message = "orderNo参数缺失")
    private String orderNo;

    @NotNull(message = "outTradeNo参数缺失")
    private String outTradeNo;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "exchangeId参数缺失")
    private Integer exchangeId;

    @NotNull(message = "exchangeName参数缺失")
    private String exchangeName;

    @NotNull(message = "exchangeImage参数缺失")
    private String exchangeImage;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "memberAddressId参数缺失")
    private Integer memberAddressId;

    @NotNull(message = "relateId参数缺失")
    private Integer relateId;

    @NotNull(message = "relateOrderId参数缺失")
    private Integer relateOrderId;

    @NotNull(message = "point参数缺失")
    private Integer point;

    @NotNull(message = "price参数缺失")
    private BigDecimal price;

    @NotNull(message = "balance参数缺失")
    private BigDecimal balance;

    @NotNull(message = "payTime参数缺失")
    private Integer payTime;

    @NotNull(message = "closeTime参数缺失")
    private Integer closeTime;

    @NotNull(message = "num参数缺失")
    private Integer num;

    @NotNull(message = "status参数缺失")
    private String status;

    @NotNull(message = "orderMoney参数缺失")
    private BigDecimal orderMoney;

}
