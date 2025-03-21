package com.niu.shop.service.api.goods.refund.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RefundDeliveryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderRefundNo;

    private Delivery delivery;

    @Data
    public static class Delivery {
        @NotNull(message = "缺少参数delivery[express_number]")
        private String expressNumber;

        @NotNull(message = "缺少参数delivery[express_company]")
        private String expressCompany;

        private String remark = "";
    }
}
