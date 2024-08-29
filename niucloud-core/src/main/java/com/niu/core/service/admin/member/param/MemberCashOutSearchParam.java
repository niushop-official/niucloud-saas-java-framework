package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 会员提现参数
 */
@Data
public class MemberCashOutSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String cashOutNo;

    private Integer memberId;

    private String accountType;

    private String transferType;

    private String transferRealname;

    private String transferMobile;

    private String transferBank;

    private String transferAccount;

    private String transferFailReason;

    private String transferStatus;

    private String[] transferTime;

    private BigDecimal applyMoney;

    private BigDecimal rate;

    private BigDecimal serviceMoney;

    private BigDecimal money;

    private Integer auditTime;

    private Integer status;

    private String remark;

    private String refuseReason;

    private String transferNo;

    private Integer cancelTime;

    private String finalTransferType;

    private String keywords;

    private String[] createTime;
}
