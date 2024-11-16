package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 优惠券领用参数
*/
@Data
public class ShopCouponReceiveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "couponId参数缺失")
    private Integer couponId;

    @NotNull(message = "number参数缺失")
    private Integer number;

    private String type = "receive";

}
