package com.niu.core.service.admin.member.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员提现参数
 */
@Data
public class MemberCashOutAuditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer Id;

    private String action;

    private String refuseReason;

}
