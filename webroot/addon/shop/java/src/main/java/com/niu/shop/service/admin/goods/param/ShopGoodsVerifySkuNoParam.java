package com.niu.shop.service.admin.goods.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品参数
*/
@Data
public class ShopGoodsVerifySkuNoParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "sku_no参数缺失")
    private String skuNo;

    @NotNull(message = "goods_id参数缺失")
    private Integer goodsId;

}
