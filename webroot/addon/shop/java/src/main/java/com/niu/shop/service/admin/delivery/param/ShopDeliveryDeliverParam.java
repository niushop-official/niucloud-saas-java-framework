package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 配送员参数
*/
@Data
public class ShopDeliveryDeliverParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "deliver_name参数缺失")
    @NotEmpty(message = "请输入配送员姓名")
    private String deliverName;

    @NotNull(message = "deliver_mobile参数缺失")
    @NotEmpty(message = "请输入配送员手机号")
    private String deliverMobile;

}
