package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 会员提现参数
 */
@Data
public class MemberCashOutSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

    private String transferType;

    private Integer status;

    private String[] createTime;

    private String[] auditTime;

    private String[] transferTime;

    private String cashOutNo;

    private String keywords;

}
