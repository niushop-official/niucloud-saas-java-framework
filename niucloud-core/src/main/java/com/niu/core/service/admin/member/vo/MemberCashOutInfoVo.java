package com.niu.core.service.admin.member.vo;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.enums.pay.TransferStatusEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * MemberCashOut视图
 */
@Data
public class MemberCashOutInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String cashOutNo;  // 提现交易号
    private Integer memberId;  // 会员id
    private String accountType;  // 提现账户类型
    private String transferType;  // 转账提现类型
    private String transferRealname;  // 联系人名称
    private String transferMobile;  // 手机号
    private String transferBank;  // 银行名称
    private String transferAccount;  // 收款账号
    private String transferFailReason;  // 失败原因
    private String transferStatus;  // 转账状态
    private Integer transferTime;  // 转账时间
    private BigDecimal applyMoney;  // 提现申请金额
    private BigDecimal rate;  // 提现手续费比率
    private BigDecimal serviceMoney;  // 提现手续费
    private BigDecimal money;  // 提现到账金额
    private Integer auditTime;  // 审核时间
    private Integer status;  // 状态1待审核2.待转账3已转账 -1拒绝 -2 已取消
    private String remark;  // 备注
    private String refuseReason;  // 拒绝理由
    private String transferNo;  // 转账单号
    private Integer cancelTime;  // 取消时间
    private String finalTransferType;  // 转账方式
    private String statusName;
    private String transferTypeName;
    private String transferStatusName;
    private String accountTypeName;
    private MemberBriefInfoVo memberInfo;

    public String getStatusName() {
        return MemberCashOutStatusEnum.getNameByStatus(this.status);
    }

    public String getRransferTypeName() {
        if (this.transferType.isEmpty()) return "";
        return TransferTypeEnum.getNameByType(this.transferType);
    }

    public String getTransferStatusName() {
        if (this.transferStatus.isEmpty()) return "";
        return TransferStatusEnum.getNameByStatus(this.transferStatus);
    }

    public String getAccountTypeName() {
        if (this.accountType.isEmpty()) return "";
        return AccountTypeEnum.getNameByType(this.accountType);
    }
}
