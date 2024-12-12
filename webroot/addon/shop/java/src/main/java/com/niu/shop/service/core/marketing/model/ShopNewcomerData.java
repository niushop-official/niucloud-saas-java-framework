package com.niu.shop.service.core.marketing.model;

import com.niu.shop.entity.active.ShopActiveGoods;
import lombok.Data;

@Data
public class ShopNewcomerData {
    private String activeGoodsInfo;
    private Integer siteId;
    private ShopActiveGoods activeGoods;
    private String activeType;
    private String activeGoodsType;
    private String activeClass;
    private ShopNewcomerActiveValue activeValue;
}
