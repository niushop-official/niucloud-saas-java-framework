package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 会员账单参数
 */
@Data
public class MemberAccountLogSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

    private Integer siteId;

    private String accountType;

    private BigDecimal accountData;

    private BigDecimal accountSum;

    private String fromType;

    private String relatedId;

    private String memo;

    private String keywords;

    private String[] createTime;
}
