package com.niu.shop.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 优惠券参数
 */
@Data
public class ShopCouponSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Integer status;


}
