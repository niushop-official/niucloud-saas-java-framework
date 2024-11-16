package com.niu.shop.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品参数
 */
@Data
public class ShopGoodsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keyword;

    private String goodsCategory;

    private Integer brandId;

    private Integer labelIds;

    private Double startPrice = 0.00;

    private Double endPrice = 0.00;

    private String order;

    private String sort = "desc";

    private Integer couponId;
}
