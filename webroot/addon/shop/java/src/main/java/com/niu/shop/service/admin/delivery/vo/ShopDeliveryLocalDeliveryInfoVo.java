package com.niu.shop.service.admin.delivery.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
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
 * ShopDeliveryLocalDelivery视图
 */
@Data
public class ShopDeliveryLocalDeliveryInfoVo implements Serializable {

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
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String deliveryType;  // 配送类型
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String area;  // 配送区域
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String center;  // 发货地址中心点

}
