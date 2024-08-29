package com.niu.recharge.service.admin.vo;

import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.entity.RechargeOrderItemRefund;
import com.niu.recharge.enums.RefundStatusEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeRefundInfoVo extends RechargeOrderItemRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    private RechargeOrderItem item;

    private String statusName;

    private MemberBriefInfoVo member;

    private String orderFromName;

    public String getStatusName() {
        return RefundStatusEnum.getNameByStatus(this.getStatus());
    }

}
