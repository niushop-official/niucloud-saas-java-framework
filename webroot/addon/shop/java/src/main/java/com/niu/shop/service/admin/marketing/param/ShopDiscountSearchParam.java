package com.niu.shop.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺营销活动（整体活动）参数
 */
@Data
public class ShopDiscountSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String activeName;

    private String activeStatus;


}
