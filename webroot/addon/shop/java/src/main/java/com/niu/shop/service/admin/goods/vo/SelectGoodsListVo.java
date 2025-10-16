package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopGoods列表视图
 */
@Data
public class SelectGoodsListVo implements Serializable {

    private Integer siteId;
    private Integer goodsId;
    private String goodsName;
    private String subTitle;
    private String goodsImage;
    private String unit;
    private String goodsVideo;
    private String goodsDesc;
    private Integer sourceId;
    private String goodsCover;
    private Integer stock;
    private String specType;
    private String goodsCoverThumbSmall;

    private List<GoodsSkuVO> skuList;
    private List<GoodsSpecVO> goodsSpec;


    @Data
    public static class GoodsSkuVO {
        private Integer goodsId;
        private Integer skuId;
        private Integer isDefault;
        private String skuName;
        private String skuImage;
        private BigDecimal price;
        private String skuSpecFormat;
        private Integer sourceSkuId;
        private Integer stock;
    }

    @Data
    public static class GoodsSpecVO {
        private Integer goodsId;
        private String specName;
        private String specValues;
    }
}
