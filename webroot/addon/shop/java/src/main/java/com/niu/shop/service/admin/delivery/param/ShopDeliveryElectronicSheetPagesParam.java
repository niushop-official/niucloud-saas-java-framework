package com.niu.shop.service.admin.delivery.param;

import lombok.Data;

@Data
public class ShopDeliveryElectronicSheetPagesParam {
    private String templateName;
    private Integer expressCompanyId;
    private Integer isNotice;
    private Integer status;
}
