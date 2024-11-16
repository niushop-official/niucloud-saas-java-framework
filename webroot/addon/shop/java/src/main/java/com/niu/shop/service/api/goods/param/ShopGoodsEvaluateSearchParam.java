package com.niu.shop.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品评价查询参数
 */
@Data
public class ShopGoodsEvaluateSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;
    private List<String> scores;

}
