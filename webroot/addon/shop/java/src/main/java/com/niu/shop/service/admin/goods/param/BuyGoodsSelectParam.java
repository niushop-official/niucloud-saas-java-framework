package com.niu.shop.service.admin.goods.param;

import lombok.Data;

@Data
public class BuyGoodsSelectParam {
    private String keyword;
    private String goodsCategory;
    private String goodsType;
    private Integer memberId;
}
