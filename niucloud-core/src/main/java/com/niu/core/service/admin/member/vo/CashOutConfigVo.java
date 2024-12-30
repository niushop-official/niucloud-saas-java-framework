package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Member视图
 */
@Data
public class CashOutConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isOpen = 0;  // 是否启用提现
    private LinkedList<String> transferType = new LinkedList<String>();  // 提现方式
    private Integer min = 0;  // 最低提现金额
    private Integer rate = 0;  // 手续费比率
    private String isAutoVerify = "0";  // 是否自动审核
    private String isAutoTransfer = "0";  // 是否自动转账
}
