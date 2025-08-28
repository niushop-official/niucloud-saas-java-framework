package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 【请填写功能名称】参数
 */
@Data
public class ShopDeliveryCompanySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String companyName;

    private String electronicSheetSwitch = "";

    private Integer siteId;

    private List<String> expressNoElectronicSheet;

}
