package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 运费模板参数
 */
@Data
public class ShopDeliveryShippingTemplateSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String templateName;

}
