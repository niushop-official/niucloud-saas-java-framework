package com.niu.shop.service.admin.goods.vo;

import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetBuyGoodsSelectedVo extends ShopGoodsSku {
    private String goodsCoverThumbSmall;
    private String goodsCoverThumbMid;
    private Integer hasBuy;
    private ShopGoods goods;
    private ShopManjianRuleContentVo manjianInfo;
}
