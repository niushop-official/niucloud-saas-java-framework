package com.niu.shop.service.admin.goods.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsCollect视图
 */
@Data
public class ShopGoodsCollectInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private Integer goodsId;  // 商品id

}
