package com.niu.shop.service.api.cart.param;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 购物车计算参数
*/
@Data
public class ShopCartCalculateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "skuIds参数缺失")
    private List<Item> skuIds;


    @Data
    public static class Item {
        private Integer skuId;
        private Integer num;
    }

}
