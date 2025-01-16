package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.util.List;

@Data
public class BuyGoodsSelectedParam {
    private List<Integer> skuIds;
    private Integer memberId;
}
