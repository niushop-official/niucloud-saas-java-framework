package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品标签参数
 */
@Data
public class ShopGoodsLabelSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String labelName;

}
