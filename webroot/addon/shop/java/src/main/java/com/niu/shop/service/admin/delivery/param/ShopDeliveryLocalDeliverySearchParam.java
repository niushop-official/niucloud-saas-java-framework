package com.niu.shop.service.admin.delivery.param;
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
 * 【请填写功能名称】参数
 */
@Data
public class ShopDeliveryLocalDeliverySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String feeType;

    private BigDecimal baseDist;

    private BigDecimal basePrice;

    private BigDecimal gradDist;

    private BigDecimal gradPrice;

    private BigDecimal weightStart;

    private BigDecimal weightUnit;

    private BigDecimal weightPrice;

    private String deliveryType;

    private String area;

    private String center;

}
