package com.niu.shop.service.admin.delivery.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ShopDeliveryElectronicSheetListVo {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("express_company_id")
    private Integer expressCompanyId;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("pay_type")
    private Integer payType;
    @JsonProperty("is_notice")
    private Integer isNotice;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("is_default")
    private Integer isDefault;
    @JsonProperty("create_time")
    private String createTime;
}
