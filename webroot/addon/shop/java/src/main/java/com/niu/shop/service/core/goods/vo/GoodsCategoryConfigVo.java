package com.niu.shop.service.core.goods.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsAttr视图
 */
@Data
public class GoodsCategoryConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer level = 2;
    private String template = "style-1";
    private String pageTitle = "商品分类";
    private Search search = new Search();
    private Goods goods = new Goods();
    private String sort = "default";
    private Cart cart = new Cart();

    @Data
    public static class Search {
        private Integer control = 1;
        private String title = "请搜索您想要的商品";
    }

    @Data
    public static class Goods {
        private String style = "single-cols";
    }

    @Data
    public static class Cart {
        private Integer control = 1;
        private String event = "detail";
        private String style = "style-1";
        private String text = "购买";
    }
}
