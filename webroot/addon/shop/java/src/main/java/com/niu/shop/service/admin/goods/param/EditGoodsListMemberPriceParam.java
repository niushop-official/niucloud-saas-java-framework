package com.niu.shop.service.admin.goods.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品参数参数
*/
@Data
public class EditGoodsListMemberPriceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_id参数缺失")
    private Integer goodsId;

    @NotNull(message = "member_discount参数缺失")
    private String memberDiscount;

    @NotNull(message = "sku_list参数缺失")
    private List<Sku> skuList;

    @Data
    public static class Sku {
        @NotNull(message = "sku_id参数缺失")
        private Integer skuId;

        @NotNull(message = "member_price参数缺失")
        private JSONObject memberPrice;
    }
}
