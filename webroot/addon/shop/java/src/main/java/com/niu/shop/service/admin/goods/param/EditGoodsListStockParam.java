package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 商品参数参数
*/
@Data
public class EditGoodsListStockParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_id参数缺失")
    private Integer goodsId;

    @NotNull(message = "sku_list参数缺失")
    private List<Sku> skuList;

    @Data
    public static class Sku {
        @NotNull(message = "sku_id参数缺失")
        private Integer skuId;

        @NotNull(message = "stock参数缺失")
        private Integer stock;
    }
}
