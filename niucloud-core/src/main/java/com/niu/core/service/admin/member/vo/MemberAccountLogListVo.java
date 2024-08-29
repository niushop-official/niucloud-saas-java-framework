package com.niu.core.service.admin.member.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * MemberAccountLog列表视图
 */
@Data
public class MemberAccountLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer memberId;  // 用户id
    private Integer siteId;  // 站点id
    private String accountType;  // 账户类型
    private BigDecimal accountData;  // 账户数据
    private BigDecimal accountSum;  // 变动后的账户余额
    private String fromType;  // 来源类型
    private String relatedId;  // 关联Id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private String memo;  // 备注信息
    private MemberBriefInfoVo member; // 会员信息
    private String accountTypeName;
    private String fromTypeName;

    public String getAccountTypeName() {
        return AccountTypeEnum.getNameByType(this.accountType);
    }

    public String getFromTypeName() {
        JSONObject changeType = MemberAccountChangeTypeEnum.getType(this.accountType);
        return changeType.getByPath(this.fromType + ".name", String.class);
    }
}
