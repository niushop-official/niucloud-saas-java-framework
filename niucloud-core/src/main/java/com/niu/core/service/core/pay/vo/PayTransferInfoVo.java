package com.niu.core.service.core.pay.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * PayTransfer视图
 */
@Data
public class PayTransferInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String tradeType;  // 业务类型
    private String transferNo;  // 转账单号
    private Integer mainId;  // 会员id
    private String mainType;  // 主体类型
    private String transferType;  // 转账类型
    private String transferRealname;  // 联系人名称
    private String transferMobile;  // 手机号
    private String transferBank;  // 银行名称
    private String transferAccount;  // 收款账号
    private String transferVoucher;  // 凭证
    private String transferRemark;  // 凭证说明
    private String transferFailReason;  // 失败原因
    private String transferStatus;  // 转账状态
    private BigDecimal money;  // 转账金额
    private Integer transferTime;  // 转账时间
    private String openid;  // 
    private String remark;  // 
    private String batchId;  // 转账批次id

}
