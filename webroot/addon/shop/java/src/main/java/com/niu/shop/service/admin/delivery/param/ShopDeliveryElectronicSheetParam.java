package com.niu.shop.service.admin.delivery.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 电子面单参数
*/
@Data
public class ShopDeliveryElectronicSheetParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    @NotNull(message = "templateName参数缺失")
    private String templateName;

    @NotNull(message = "expressCompanyId参数缺失")
    private Integer expressCompanyId;

    private String customerName;

    private String customerPwd;

    private String sendSite;

    private String sendStaff;

    private String monthCode;

    @NotNull(message = "payType参数缺失")
    private Integer payType;

    private Integer isNotice;

    private Integer status;

    @NotNull(message = "expType参数缺失")
    private Integer expType;

    @NotNull(message = "printStyle参数缺失")
    private String printStyle;

    private Integer isDefault;

}
