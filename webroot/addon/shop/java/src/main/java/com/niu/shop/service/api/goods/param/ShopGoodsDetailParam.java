package com.niu.shop.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数
 */
@Data
public class ShopGoodsDetailParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;

    private Integer skuId;

    private String type;

}
