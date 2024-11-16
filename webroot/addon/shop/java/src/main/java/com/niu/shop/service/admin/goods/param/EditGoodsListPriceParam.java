package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品参数参数
*/
@Data
public class EditGoodsListPriceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_id参数缺失")
    private Integer goodsId;

    @NotNull(message = "sku_list参数缺失")
    private List<Sku> skuList;

    @Data
    public static class Sku {
        @NotNull(message = "sku_id参数缺失")
        private Integer skuId;

        @NotNull(message = "price参数缺失")
        private BigDecimal price;  // sku单价

        @NotNull(message = "market_price参数缺失")
        private BigDecimal marketPrice;  // 划线价

        @NotNull(message = "cost_price参数缺失")
        private BigDecimal costPrice;
    }
}
