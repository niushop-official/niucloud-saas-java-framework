package com.niu.core.service.admin.pay.vo;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.common.CommonActiveEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.pay.RefundStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * PayRefund列表视图
 */
@Data
public class PayRefundListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer siteId;  // 站点id
    private String refundNo;  // 退款单号
    private String outTradeNo;  // 支付流水号
    private String type;  // 支付方式
    private String channel;  // 支付渠道
    private BigDecimal money;  // 支付金额
    private String reason;  // 退款原因
    private String status;  // 支付状态（0.待退款 1. 退款中 2. 已退款 -1已关闭）
    private Integer refundTime;  // 支付时间
    private Integer closeTime;  // 关闭时间
    private String failReason;  // 失败原因
    private String voucher;  // 支付凭证
    private String tradeType;  // 业务类型
    private String tradeId;  // 业务关联id
    private String refundType;  // 退款方式
    private String mainType;  // 操作人类型
    private Integer mainId;  // 操作人
    private String payRefundNo;  // 外部支付方式的退款单号
    private String statusName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    private String typeName;
    private String tradeTypeName;

    public String getStatusName() {
        return RefundStatusEnum.getNameByStatus(this.status);
    }

    public String getTypeName() {
        if (ObjectUtil.isEmpty(this.type)) {
            return "";
        }
        return PayTypeEnum.getType().getByPath(this.type + ".name", String.class);
    }

    public String getTradeTypeName() {
        if (ObjectUtil.isEmpty(this.tradeType)) {
            return "";
        }
        Map<String, Object> active = CommonActiveEnum.getActiveShort(this.tradeType);
        return active == null ? "" : active.get("active_name").toString();
    }
}
