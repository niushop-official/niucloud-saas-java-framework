package com.niu.shop.service.admin.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品排行榜详情
 */
@Data
public class ShopGoodsRankInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     *
     */
    private Integer rankId;


    /**
     * 站点id
     */
    private Integer siteId;


    /**
     * 榜单名称
     */
    private String name;


    /**
     * 排行周期 day=天，week=周，month=月, quarter=季度
     */
    private String rankType;


    /**
     * 来源类型 goods=指定商品，category=指定分类，brand=指定品牌, label=指定标签
     */
    private String goodsSource;


    /**
     * 排序规则 sale=按照销量，collect=按收藏数，evaluate=按评价数, access=按照浏览量
     */
    private String ruleType;


    /**
     * 商品信息
     */
    private List<?> goodsJson;


    /**
     * 商品分类id
     */
    private List<?> categoryIds;


    /**
     * 商品品牌id
     */
    private List<?> brandIds;


    /**
     * 商品标签id，多个逗号隔开
     */
    private List<?> labelIds;


    /**
     * 排序号
     */
    private Integer sort;


    /**
     * 显示状态（0不显示 1显示）
     */
    private Integer status;


    /**
     * 创建时间
     */
    private String createTime;


    /**
     * 修改时间
     */
    private String updateTime;

    private List<?> goodsList;

    @NoArgsConstructor
    @Data
    public static class GoodsList {

        @JsonProperty("goods_type_name")
        private String goodsTypeName;
        @JsonProperty("goods_id")
        private Integer goodsId;
        @JsonProperty("goods_name")
        private String goodsName;
        @JsonProperty("goods_type")
        private String goodsType;
        @JsonProperty("goodsSku")
        private GoodsSkuDTO goodsSku;
        @JsonProperty("sort")
        private Integer sort;

        @NoArgsConstructor
        @Data
        public static class GoodsSkuDTO {
            @JsonProperty("sku_id")
            private Integer skuId;
            @JsonProperty("sku_name")
            private String skuName;
            @JsonProperty("sku_image")
            private String skuImage;
            @JsonProperty("goods_id")
            private Integer goodsId;
            @JsonProperty("price")
            private String price;
            @JsonProperty("stock")
            private Integer stock;
        }
    }

    @Data
    public static class GoodsJson {
        @JsonProperty("goods_id")
        private Integer goodsId;
    }
}