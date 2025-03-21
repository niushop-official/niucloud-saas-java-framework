package com.niu.core.service.admin.member.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * 会员参数
*/
@Data
public class AdjustAccountParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "member_id参数缺失")
    private Integer memberId;

    @NotNull(message = "account_data参数缺失")
    private Double accountData;

    @NotNull(message = "memo参数缺失")
    private String memo = "";
}
