package com.niu.shop.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数
 */
@Data
public class ShopGoodsCategorySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pid;
}
