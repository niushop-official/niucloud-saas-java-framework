package com.niu.core.service.api.member.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBalanceListVo {

    private Integer id;

    //  用户id
    private Integer memberId;

    // 站点id
    private Integer siteId;

    // 账户类型
    private String accountType;

    // 账户数据
    private BigDecimal accountData;

    // 来源类型
    private String fromType;

    // 关联Id
    private String relatedId;

    // 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    // 备注信息
    private String memo;

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
