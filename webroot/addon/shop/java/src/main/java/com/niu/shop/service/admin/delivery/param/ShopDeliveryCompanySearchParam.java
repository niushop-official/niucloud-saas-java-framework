package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 【请填写功能名称】参数
 */
@Data
public class ShopDeliveryCompanySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String companyName;

    private String electronicSheetSwitch = "";

}
