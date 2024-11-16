package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 订单批量发货参数
*/
@Data
public class ShopOrderBatchDeliveryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "mainId参数缺失")
    private Integer mainId;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "totalNum参数缺失")
    private Integer totalNum;

    @NotNull(message = "successNum参数缺失")
    private Integer successNum;

    @NotNull(message = "failNum参数缺失")
    private Integer failNum;

    @NotNull(message = "data参数缺失")
    private String data;

    @NotNull(message = "output参数缺失")
    private String output;

    @NotNull(message = "failOutput参数缺失")
    private String failOutput;

    @NotNull(message = "failRemark参数缺失")
    private String failRemark;

}
