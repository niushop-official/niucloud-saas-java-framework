package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品评价参数
 */
@Data
public class ShopGoodsEvaluateSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String goodsName;

    private Integer status;

}
