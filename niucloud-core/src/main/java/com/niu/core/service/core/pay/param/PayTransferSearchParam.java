package com.niu.core.service.core.pay.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 转账参数
 */
@Data
public class PayTransferSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String tradeType;

    private String transferNo;

    private Integer mainId;

    private String mainType;

    private String transferType;

    private String transferRealname;

    private String transferMobile;

    private String transferBank;

    private String transferAccount;

    private String transferVoucher;

    private String transferRemark;

    private String transferFailReason;

    private String transferStatus;

    private BigDecimal money;

    private Integer transferTime;

    private String openid;

    private String remark;

    private String batchId;

}
