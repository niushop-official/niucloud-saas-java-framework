package com.niu.shop.service.api.refund.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

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
