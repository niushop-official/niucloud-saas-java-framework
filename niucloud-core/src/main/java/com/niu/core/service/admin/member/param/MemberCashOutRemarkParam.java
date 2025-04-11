package com.niu.core.service.admin.member.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员提现参数
*/
@Data
public class MemberCashOutRemarkParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String remark;

}
