package com.niu.shop.service.admin.delivery.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.delivery.ShippingTemplateEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopDeliveryShippingTemplate列表视图
 */
@Data
public class ShopDeliveryShippingTemplateListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer templateId;  // 
    private Integer siteId;  // 站点id
    private String templateName;  // 模板名称
    private String feeType;  // 运费计算方式1.重量2体积3按件
    private String feeTypeName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 修改时间
    private Integer isFreeShipping;  // 该区域是否包邮
    private Integer noDelivery;  // 是否指定该区域不配送

    public String getFeeTypeName() {
        return ShippingTemplateEnum.getNameByType(this.feeType);
    }
}
