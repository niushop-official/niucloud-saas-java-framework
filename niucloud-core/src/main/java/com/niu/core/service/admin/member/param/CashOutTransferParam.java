package com.niu.core.service.admin.member.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员标签参数
*/
@Data
public class CashOutTransferParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "transfer_voucher参数缺失")
    private String transferVoucher;

    private String transferRemark = "";

    @NotNull(message = "transfer_type参数缺失")
    private String transferType;

}
