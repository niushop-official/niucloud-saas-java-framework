package com.niu.shop.service.admin.delivery.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * ShopDeliveryLocalDelivery列表视图
 */
@Data
public class ShopDeliveryLocalDeliveryListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer localId;  // 
    private Integer siteId;  // 站点id
    private String feeType;  // 费用类型
    private BigDecimal baseDist;  // 多少km内
    private BigDecimal basePrice;  // 配送费用
    private BigDecimal gradDist;  // 每超出多少km内
    private BigDecimal gradPrice;  // 配送费用
    private BigDecimal weightStart;  // 重量多少内不额外收费
    private BigDecimal weightUnit;  // 每超出多少kg
    private BigDecimal weightPrice;  // 价格
    private String deliveryType;  // 配送类型
    private String area;  // 配送区域
    private String center;  // 发货地址中心点

}
