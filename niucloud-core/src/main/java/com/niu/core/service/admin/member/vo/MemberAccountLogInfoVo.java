package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * MemberAccountLog视图
 */
@Data
public class MemberAccountLogInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer memberId;  // 用户id
    private Integer siteId;  // 站点id
    private String accountType;  // 账户类型
    private BigDecimal accountData;  // 账户数据
    private BigDecimal accountSum;  // 变动后的账户余额
    private String fromType;  // 来源类型
    private String relatedId;  // 关联Id
    private String memo;  // 备注信息

}
