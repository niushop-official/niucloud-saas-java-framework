package com.niu.shop.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品参数
 */
@Data
public class ShopGoodsComponentsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsCategory = 0;

    private String order;

    private Integer[] goodsIds;

    private Integer num = 0;
}
