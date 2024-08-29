package com.niu.recharge.service.admin.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.entity.RechargeOrderItemRefund;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.enums.RefundStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RechargeRefundListVo extends RechargeOrderItemRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    private RechargeOrderItem item;

    private String statusName;

    private MemberBriefInfoVo member;

    private String orderFromName;

    public String getStatusName() {
        return RefundStatusEnum.getNameByStatus(this.getStatus());
    }

}
