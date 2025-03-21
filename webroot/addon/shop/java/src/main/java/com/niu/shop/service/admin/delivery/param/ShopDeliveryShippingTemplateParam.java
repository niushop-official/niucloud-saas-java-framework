package com.niu.shop.service.admin.delivery.param;
import cn.hutool.json.JSONArray;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 运费模板参数
*/
@Data
public class ShopDeliveryShippingTemplateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "template_name参数缺失")
    @NotEmpty(message = "模板名称不能为空")
    private String templateName;

    @NotNull(message = "fee_type参数缺失")
    private String feeType;

    private Integer isFreeShipping = 0;

    private Integer noDelivery = 0;

    private JSONArray area;

}
