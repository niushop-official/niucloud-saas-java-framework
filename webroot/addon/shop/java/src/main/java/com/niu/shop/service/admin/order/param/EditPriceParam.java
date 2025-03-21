package com.niu.shop.service.admin.order.param;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class EditPriceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "order_id参数缺失")
    private Integer orderId;

    @NotNull(message = "delivery_money参数缺失")
    @Min(value = 0, message = "运费不能小于0")
    private BigDecimal deliveryMoney;

    @NotNull(message = "order_goods_data参数缺失")
    private Map<Integer, EditData> orderGoodsData;

    @Data
    public static class EditData {
        @NotNull(message = "money参数缺失")
        private BigDecimal money;
    }
}
