package com.niu.shop.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数
 */
@Data
public class ShopGoodsComponentsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsCategory = 0;

    private String order;

    private String goodsIds = "";

    private Integer num = 0;
}
