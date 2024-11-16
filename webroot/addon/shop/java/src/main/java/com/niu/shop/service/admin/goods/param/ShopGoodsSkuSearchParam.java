package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 商品规格参数
 */
@Data
public class ShopGoodsSkuSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String skuName;

    private String skuImage;

    private String skuNo;

    private Integer goodsId;

    private String skuSpecFormat;

    private BigDecimal price;

    private BigDecimal marketPrice;

    private BigDecimal salePrice;

    private BigDecimal costPrice;

    private Integer stock;

    private BigDecimal weight;

    private BigDecimal volume;

    private Integer saleNum;

    private Integer isDefault;

    private String memberPrice;

}
