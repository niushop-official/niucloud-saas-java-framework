package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/**
 * 商品规格参数
*/
@Data
public class ShopGoodsSkuParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "skuId参数缺失")
    private Integer skuId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "skuName参数缺失")
    private String skuName;

    @NotNull(message = "skuImage参数缺失")
    private String skuImage;

    @NotNull(message = "skuNo参数缺失")
    private String skuNo;

    @NotNull(message = "goodsId参数缺失")
    private Integer goodsId;

    @NotNull(message = "skuSpecFormat参数缺失")
    private String skuSpecFormat;

    @NotNull(message = "price参数缺失")
    private BigDecimal price;

    @NotNull(message = "marketPrice参数缺失")
    private BigDecimal marketPrice;

    @NotNull(message = "salePrice参数缺失")
    private BigDecimal salePrice;

    @NotNull(message = "costPrice参数缺失")
    private BigDecimal costPrice;

    @NotNull(message = "stock参数缺失")
    private Integer stock;

    @NotNull(message = "weight参数缺失")
    private BigDecimal weight;

    @NotNull(message = "volume参数缺失")
    private BigDecimal volume;

    @NotNull(message = "saleNum参数缺失")
    private Integer saleNum;

    @NotNull(message = "isDefault参数缺失")
    private Integer isDefault;

    @NotNull(message = "memberPrice参数缺失")
    private String memberPrice;

}
