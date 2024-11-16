package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 电子面单参数
 */
@Data
public class ShopDeliveryElectronicSheetListsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String templateName;

    private Integer expressCompanyId;

    private String customerName;

    private String customerPwd;

    private String sendSite;

    private String sendStaff;

    private String monthCode;

    private Integer payType;

    private Integer isNotice;

    private Integer status;

    private Integer expType;

    private String printStyle;

    private Integer isDefault;

}
