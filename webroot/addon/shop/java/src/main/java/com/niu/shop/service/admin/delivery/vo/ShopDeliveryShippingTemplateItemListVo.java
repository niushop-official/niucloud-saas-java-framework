package com.niu.shop.service.admin.delivery.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * ShopDeliveryShippingTemplateItem列表视图
 */
@Data
public class ShopDeliveryShippingTemplateItemListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer itemId;  // 
    private Integer siteId;  // 站点id
    private Integer templateId;  // 模板id
    private Integer cityId;  // 市id
    private Integer snum;  // 起步计算标准
    private BigDecimal sprice;  // 起步计算价格
    private Integer xnum;  // 续步计算标准
    private BigDecimal xprice;  // 续步计算价格
    private String feeType;  // 运费计算方式
    private String feeAreaIds;  // 运费设置区域id集
    private String feeAreaNames;  // 运费设置区域名称集
    private Integer noDelivery;  // 是否指定该区域不配送
    private String noDeliveryAreaIds;  // 不配送的区域id集
    private String noDeliveryAreaNames;  // 不配送的区域名称集
    private Integer isFreeShipping;  // 该区域是否包邮
    private String freeShippingAreaIds;  // 包邮的区域id集
    private String freeShippingAreaNames;  // 包邮的区域名称集
    private BigDecimal freeShippingPrice;  // 满足包邮的条件
    private Integer freeShippingNum;  // 

}
