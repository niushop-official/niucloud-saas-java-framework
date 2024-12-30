package com.niu.core.service.api.member.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberCashOutListVo {

    private Integer id;

    // 站点id
    private Integer siteId;

    // 提现交易号
    private String cashOutNo;

    // 会员id
    private Integer memberId;

    // 提现账户类型
    private String accountType;

    // 转账提现类型
    private String transferType;

    // 联系人名称
    private String transferRealname;

    // 联系人手机号
    private String transferMobile;

    // 联系人银行名称
    private String transferBank;

    // 收款账号
    private String transferAccount;

    // 转账状态
    private String transferStatus;

    // 申请时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    // 转账时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long transferTime;

    // 失败原因
    private String transferFailReason;

    // 提现申请金额
    private BigDecimal applyMoney;

    // 提现手续费比率
    private BigDecimal rate;

    // 提现手续费
    private BigDecimal serviceMoney;

    // 提现到账金额
    private BigDecimal money;

    // 审核时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long auditTime;

    // 状态1待审核2.待转账3已转账 -1拒绝 -2 已取消
    private Integer status;

    // 备注
    private String remark;

    // 拒绝理由
    private String refuseReason;

    /**
     * @return
     */
    public String getAccountTypeName() {
        return AccountTypeEnum.getNameByType(this.accountType);
    }

    /**
     * @return
     */
    public String getTransferTypeName() {
        return TransferTypeEnum.getNameByType(this.transferType);
    }

    public String getStatusName() {
        // 状态1待审核2.待转账3已转账 -1拒绝 -2 已取消
        switch (this.getStatus()) {
            case 1:
                return "待审核";
            case 2:
                return "待转账";
            case 3:
                return "已转账";
            case -1:
                return "拒绝";
            case -2:
                return "已取消";
        }
        return "";
    }

    /**
     * @return
     */
    public String getTransferStatusName() {
        return "";
    }

}
