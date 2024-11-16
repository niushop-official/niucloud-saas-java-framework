package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 订单项优惠参数
*/
@Data
public class ShopOrderDiscountGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "orderDiscountId参数缺失")
    private Integer orderDiscountId;

    @NotNull(message = "orderId参数缺失")
    private Integer orderId;

    @NotNull(message = "orderGoodsId参数缺失")
    private String orderGoodsId;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "num参数缺失")
    private Integer num;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

}
