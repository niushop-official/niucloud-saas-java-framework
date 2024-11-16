package com.niu.shop.service.admin.delivery.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopDeliveryElectronicSheet视图
 */
@Data
public class ShopDeliveryElectronicSheetInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String templateName;  // 模板名称
    private Integer expressCompanyId;  // 物流公司id
    private String customerName;  // 电子面单客户账号（CustomerName）
    private String customerPwd;  // 电子面单密码（CustomerPwd）
    private String sendSite;  // SendSite
    private String sendStaff;  // SendStaff
    private String monthCode;  // MonthCode
    private Integer payType;  // 邮费支付方式（1：现付，2：到付，3：月结）
    private Integer isNotice;  // 快递员上门揽件（0：否，1：是）
    private Integer status;  // 状态（1：开启，0：关闭）
    private Integer expType;  // 物流公司业务类型
    private String printStyle;  // 电子面单打印模板样式
    private Integer isDefault;  // 是否默认（1：是，0：否）

}
