package com.niu.core.service.core.pay.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 转账参数
*/
@Data
public class PayTransferParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "tradeType参数缺失")
    private String tradeType;

    @NotNull(message = "transferNo参数缺失")
    private String transferNo;

    @NotNull(message = "mainId参数缺失")
    private Integer mainId;

    @NotNull(message = "mainType参数缺失")
    private String mainType;

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

    @NotNull(message = "transferVoucher参数缺失")
    private String transferVoucher;

    @NotNull(message = "transferRemark参数缺失")
    private String transferRemark;

    @NotNull(message = "transferFailReason参数缺失")
    private String transferFailReason;

    @NotNull(message = "transferStatus参数缺失")
    private String transferStatus;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

    @NotNull(message = "transferTime参数缺失")
    private Long transferTime;

    @NotNull(message = "openid参数缺失")
    private String openid;

    @NotNull(message = "remark参数缺失")
    private String remark;

    @NotNull(message = "batchId参数缺失")
    private String batchId;

}
