package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品收藏记录参数
 */
@Data
public class ShopGoodsCollectSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer memberId;

    private Integer goodsId;

}
