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
public class Pay implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 支付会员id */
    private Integer mainId;

    /** 支付流水号 */
    private String outTradeNo;

    /** 业务类型 */
    private String tradeType;

    /** 业务id */
    private Integer tradeId;

    /** 交易单号 */
    private String tradeNo;

    /** 支付主体 */
    private String body;

    /** 支付金额 */
    private BigDecimal money;

    /** 支付票据 */
    private String voucher;

    /** 支付状态（0.待支付 1. 支付中 2. 已支付 -1已取消） */
    private Integer status;

    /** 支付扩展用支付信息 */
    private String json;

    /** 创建时间 */
    private Long createTime;

    /** 支付时间 */
    private Long payTime;

    /** 关闭时间 */
    private Long cancelTime;

    /** 支付方式 */
    private String type;

    /** 商户收款账号 */
    private String mchId;

    private String mainType;

    /** 支付渠道 */
    private String channel;

    /** 失败原因 */
    private String failReason;

}