package com.niu.shop.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 积分兑换订单参数
 */
@Data
public class ShopPointExchangeOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String outTradeNo;

    private Integer siteId;

    private Integer exchangeId;

    private String exchangeName;

    private String exchangeImage;

    private String type;

    private Integer memberId;

    private Integer memberAddressId;

    private Integer relateId;

    private Integer relateOrderId;

    private Integer point;

    private BigDecimal price;

    private BigDecimal balance;

    private Integer payTime;

    private Integer closeTime;

    private Integer num;

    private String status;

    private BigDecimal orderMoney;

}
