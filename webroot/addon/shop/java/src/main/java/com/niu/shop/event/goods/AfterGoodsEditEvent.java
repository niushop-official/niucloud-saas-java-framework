package com.niu.shop.event.goods;

import com.niu.core.common.component.context.event.Event;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import lombok.Data;

import java.util.List;

@Data
public class AfterGoodsEditEvent extends Event {

    private Integer goodsId;

    private ShopGoods goodsData;

    private List<ShopGoodsSku> skuData;
}
