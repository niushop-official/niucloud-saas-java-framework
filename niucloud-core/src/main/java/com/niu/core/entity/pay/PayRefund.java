package com.niu.core.entity.pay;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付记录实体
 */
@Data
public class PayRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 退款单号 */
    private String refundNo;

    /** 支付流水号 */
    private String outTradeNo;

    /** 支付方式 */
    private String type;

    /** 支付渠道 */
    private String channel;

    /** 支付金额 */
    private BigDecimal money;

    /** 退款原因 */
    private String reason;

    /** 支付状态（0.待退款 1. 退款中 2. 已退款 -1已关闭） */
    private String status;

    /** 创建时间 */
    private Long createTime;

    /** 支付时间 */
    private Long refundTime;

    /** 关闭时间 */
    private Long closeTime;

    /** 失败原因 */
    private String failReason;

    /** 支付凭证 */
    private String voucher;

    /** 业务类型 */
    private String tradeType;

    /** 业务关联id */
    private String tradeId;

    /** 退款方式 */
    private String refundType;

    /** 操作人类型 */
    private String mainType;

    /** 操作人 */
    private Integer mainId;

    /** 外部支付方式的退款单号 */
    private String payRefundNo;

}