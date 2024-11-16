package com.niu.shop.service.api.goods;

import com.niu.shop.service.api.goods.vo.ShopGoodsMemberInfoVo;

import java.math.BigDecimal;

public interface IShopGoodsService {
    ShopGoodsMemberInfoVo getMemberInfo();

    BigDecimal getMemberPrice(ShopGoodsMemberInfoVo memberInfo, String memberDiscount, String memberPrice, String price);
}
