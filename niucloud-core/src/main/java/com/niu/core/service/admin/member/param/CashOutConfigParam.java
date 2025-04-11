package com.niu.core.service.admin.member.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * 会员参数
*/
@Data
public class CashOutConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "is_open参数缺失")
    private String isOpen = "0";  // 是否启用提现

    @NotNull(message = "transfer_type参数缺失")
    private LinkedList<String> transferType = new LinkedList<String>();  // 提现方式

    @NotNull(message = "min参数缺失")
    private BigDecimal min = BigDecimal.ZERO;  // 最低提现金额

    @NotNull(message = "rate参数缺失")
    private BigDecimal rate = BigDecimal.ZERO;  // 手续费比率

    @NotNull(message = "is_auto_verify参数缺失")
    private String isAutoVerify = "0";  // 是否自动审核

    @NotNull(message = "is_auto_transfer参数缺失")
    private String isAutoTransfer = "0";  // 是否自动转账

}
