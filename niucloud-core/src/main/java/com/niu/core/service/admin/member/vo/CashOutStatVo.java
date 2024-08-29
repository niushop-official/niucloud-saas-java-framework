package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Member视图
 */
@Data
public class CashOutStatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal transfered = new BigDecimal(0); // 累计提现
    private BigDecimal cashOuting = new BigDecimal(0);  // 是否自动转账
}
