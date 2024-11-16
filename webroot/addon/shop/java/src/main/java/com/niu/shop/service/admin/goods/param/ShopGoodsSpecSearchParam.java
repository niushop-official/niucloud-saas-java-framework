package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品规格项/值参数
 */
@Data
public class ShopGoodsSpecSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer goodsId;

    private String specName;

    private String specValues;

}
