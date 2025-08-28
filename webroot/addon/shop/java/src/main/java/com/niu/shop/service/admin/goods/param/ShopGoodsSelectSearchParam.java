package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品参数
 */
@Data
public class ShopGoodsSelectSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keyword;

    private Integer goodsCategory;

    private String goodsName;

    private String goodsType;

    private String selectType = "all";

    private List<Integer> goodsIds;

    private List<Integer> skuIds;

    private List<Integer> verifyGoodsIds;

    private List<Integer> verifySkuIds;

    private Integer isGift;
}
