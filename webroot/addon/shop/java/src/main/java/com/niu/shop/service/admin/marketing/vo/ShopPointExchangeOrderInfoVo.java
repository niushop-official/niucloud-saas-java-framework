package com.niu.shop.service.admin.marketing.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * ShopPointExchangeOrder视图
 */
@Data
public class ShopPointExchangeOrderInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;  // 兑换记录id
    private String orderNo;  // 订单编号
    private String outTradeNo;  // 支付流水表
    private Integer siteId;  // 站点id
    private Integer exchangeId;  // 兑换活动id
    private String exchangeName;  // 兑换商品名称
    private String exchangeImage;  // 兑换商品图片
    private String type;  // 兑换类型
    private Integer memberId;  // 消费会员id
    private Integer memberAddressId;  // 会员地址id
    private Integer relateId;  // 关联业务id
    private Integer relateOrderId;  // 关联订单id
    private Integer point;  // 使用积分
    private BigDecimal price;  // 支付金额
    private BigDecimal balance;  // 赠送余额
    private Integer payTime;  // 兑换时间
    private Integer closeTime;  // 关闭时间
    private Integer num;  // 兑换数量
    private String status;  // 订单状态
    private BigDecimal orderMoney;  // 订单金额

}
