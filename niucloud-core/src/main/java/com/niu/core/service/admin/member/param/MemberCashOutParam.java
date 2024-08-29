package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 会员提现参数
*/
@Data
public class MemberCashOutParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "cashOutNo参数缺失")
    private String cashOutNo;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "accountType参数缺失")
    private String accountType;

    @NotNull(message = "transferType参数缺失")
    private String transferType;

    @NotNull(message = "transferRealname参数缺失")
    private String transferRealname;

    @NotNull(message = "transferMobile参数缺失")
    private String transferMobile;

    @NotNull(message = "transferBank参数缺失")
    private String transferBank;

    @NotNull(message = "transferAccount参数缺失")
    private String transferAccount;

    @NotNull(message = "transferFailReason参数缺失")
    private String transferFailReason;

    @NotNull(message = "transferStatus参数缺失")
    private String transferStatus;

    @NotNull(message = "transferTime参数缺失")
    private Integer transferTime;

    @NotNull(message = "applyMoney参数缺失")
    private BigDecimal applyMoney;

    @NotNull(message = "rate参数缺失")
    private BigDecimal rate;

    @NotNull(message = "serviceMoney参数缺失")
    private BigDecimal serviceMoney;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

    @NotNull(message = "auditTime参数缺失")
    private Integer auditTime;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "remark参数缺失")
    private String remark;

    @NotNull(message = "refuseReason参数缺失")
    private String refuseReason;

    @NotNull(message = "transferNo参数缺失")
    private String transferNo;

    @NotNull(message = "cancelTime参数缺失")
    private Integer cancelTime;

    @NotNull(message = "finalTransferType参数缺失")
    private String finalTransferType;

}
