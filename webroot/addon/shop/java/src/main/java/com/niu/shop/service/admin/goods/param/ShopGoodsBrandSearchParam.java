package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品品牌参数
 */
@Data
public class ShopGoodsBrandSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brandName;

}
