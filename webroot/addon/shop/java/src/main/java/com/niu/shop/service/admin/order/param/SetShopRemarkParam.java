package com.niu.shop.service.admin.order.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SetShopRemarkParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "order_id参数缺失")
    private Integer orderId;

    @NotNull(message = "shop_remark参数缺失")
    private String shopRemark;
}
