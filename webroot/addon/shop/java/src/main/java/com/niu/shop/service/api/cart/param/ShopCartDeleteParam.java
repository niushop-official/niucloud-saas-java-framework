package com.niu.shop.service.api.cart.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购物车参数
*/
@Data
public class ShopCartDeleteParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids参数缺失")
    private Object ids;

}
