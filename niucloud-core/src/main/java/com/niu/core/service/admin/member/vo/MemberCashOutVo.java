package com.niu.core.service.admin.member.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员提现实体
 */
@Data
public class MemberCashOutVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 提现交易号 */
    private String cashOutNo;

    /** 会员id */
    private Integer memberId;

    /** 提现账户类型 */
    private String accountType;

    /** 转账提现类型 */
    private String transferType;

    /** 联系人名称 */
    private String transferRealname;

    /** 手机号 */
    private String transferMobile;

    /** 银行名称 */
    private String transferBank;

    /** 收款账号 */
    private String transferAccount;

    /** 失败原因 */
    private String transferFailReason;

    /** 转账状态 */
    private String transferStatus;

    /** 转账时间 */
    private Integer transferTime;

    /** 提现申请金额 */
    private BigDecimal applyMoney;

    /** 提现手续费比率 */
    private BigDecimal rate;

    /** 提现手续费 */
    private BigDecimal serviceMoney;

    /** 提现到账金额 */
    private BigDecimal money;

    /** 审核时间 */
    private Long auditTime;

    /** 状态1待审核2.待转账3已转账 -1拒绝 -2 已取消 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 申请时间 */
    private Long createTime;

    /** 拒绝理由 */
    private String refuseReason;

    private Long updateTime;

    /** 转账单号 */
    private String transferNo;

    /** 取消时间 */
    private Long cancelTime;

    /** 转账方式 */
    private String finalTransferType;

    /** 会员编号 */
    private String memberNo;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 手机号 */
    private String mobile;

    /** 头像 */
    private String headimg;

}