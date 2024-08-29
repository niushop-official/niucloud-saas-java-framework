package com.niu.core.service.core.pay.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Pay列表视图
 */
@Data
public class PayListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer siteId;  // 站点id
    private Integer mainId;  // 支付会员id
    private String outTradeNo;  // 支付流水号
    private String tradeType;  // 业务类型
    private Integer tradeId;  // 业务id
    private String tradeNo;  // 交易单号
    private String body;  // 支付主体
    private BigDecimal money;  // 支付金额
    private String voucher;  // 支付票据
    private Integer status;  // 支付状态（0.待支付 1. 支付中 2. 已支付 -1已取消）
    private String json;  // 支付扩展用支付信息
    private String createTime; // 创建时间
    private Integer payTime;  // 支付时间
    private Integer cancelTime;  // 关闭时间
    private String type;  // 支付方式
    private String mchId;  // 商户收款账号
    private String mainType;  // 
    private String channel;  // 支付渠道
    private String failReason;  // 失败原因

}
