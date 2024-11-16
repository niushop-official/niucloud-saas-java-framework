package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品参数参数
 */
@Data
public class ShopGoodsAttrSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String attrName;

}
