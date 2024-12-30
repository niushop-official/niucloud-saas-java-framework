package com.niu.recharge.service.admin.param;

import lombok.Data;

@Data
public class RechargeModifyStatusParam {
    private Integer rechargeId;
    private Integer status;
}
