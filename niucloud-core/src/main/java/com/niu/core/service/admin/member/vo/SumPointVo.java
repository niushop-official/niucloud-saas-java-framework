package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Member视图
 */
@Data
public class SumPointVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pointGet;  // 可用余额
    private Integer pointUse;  // 可用余额（可提现）
}
