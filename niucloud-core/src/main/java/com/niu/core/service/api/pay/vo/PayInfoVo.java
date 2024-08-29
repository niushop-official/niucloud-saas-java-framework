package com.niu.core.service.api.pay.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PayInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
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
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 支付时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;

    /** 关闭时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
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

    private List<PayTypeVo> payTypeList;


}
