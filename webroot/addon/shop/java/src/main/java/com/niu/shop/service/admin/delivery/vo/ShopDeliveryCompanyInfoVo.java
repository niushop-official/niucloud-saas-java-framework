package com.niu.shop.service.admin.delivery.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopDeliveryCompany视图
 */
@Data
public class ShopDeliveryCompanyInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer companyId;  // 
    private Integer siteId;  // 站点id
    private String companyName;  // 物流公司名称
    private String logo;  // 物流公司logo
    private String url;  // 物流公司网站
    private String expressNo;  // 物流公司编号(用于物流跟踪)
    private String expressNoElectronicSheet;  // 物流公司编号(用于电子面单)
    private Integer electronicSheetSwitch;  // 是否支持电子面单（0：不支持，1：支持）
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String printStyle;  // 电子面单打印模板样式，json字符串
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String expType;  // 物流公司业务类型，json字符串

}
