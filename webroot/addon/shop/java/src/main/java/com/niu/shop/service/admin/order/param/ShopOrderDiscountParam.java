package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 订单优惠参数
*/
@Data
public class ShopOrderDiscountParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "orderId参数缺失")
    private Integer orderId;

    @NotNull(message = "orderGoodsIds参数缺失")
    private String orderGoodsIds;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "num参数缺失")
    private Integer num;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

    @NotNull(message = "discountType参数缺失")
    private String discountType;

    @NotNull(message = "discountTypeId参数缺失")
    private Integer discountTypeId;

    @NotNull(message = "content参数缺失")
    private String content;

    @NotNull(message = "status参数缺失")
    private Integer status;

}
