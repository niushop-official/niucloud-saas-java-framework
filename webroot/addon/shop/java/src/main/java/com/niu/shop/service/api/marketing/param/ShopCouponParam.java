package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠券参数
*/
@Data
public class ShopCouponParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @NotNull(message = "title参数缺失")
    private String title;


    @NotNull(message = "remainCount参数缺失")
    private Integer remainCount;

    @NotNull(message = "limitCount参数缺失")
    private Integer limitCount;

    @NotNull(message = "price参数缺失")
    private BigDecimal price;

    @NotNull(message = "minConditionMoney参数缺失")
    private BigDecimal minConditionMoney;

    @NotNull(message = "type参数缺失")
    private Integer type;

    @NotNull(message = "receiveType参数缺失")
    private Integer receiveType;

    private String[] receiveTime;

    private Integer receiveTypeTime;

    @NotNull(message = "validType参数缺失")
    private Integer validType;

    private String validTime;

    @NotNull(message = "length参数缺失")
    private Integer length;

    private Integer threshold;

    private Integer limit;

    private Integer[] goodsIds;

    private Integer[] goodsCategoryIds;

}
