package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 订单发货参数
*/
@Data
public class ShopOrderDeliveryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "orderId参数缺失")
    private Integer orderId;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "deliveryType参数缺失")
    private String deliveryType;

    @NotNull(message = "subDeliveryType参数缺失")
    private String subDeliveryType;

    @NotNull(message = "expressCompanyId参数缺失")
    private Integer expressCompanyId;

    @NotNull(message = "expressNumber参数缺失")
    private String expressNumber;

    @NotNull(message = "localDeliverId参数缺失")
    private Integer localDeliverId;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "remark参数缺失")
    private String remark;

}
