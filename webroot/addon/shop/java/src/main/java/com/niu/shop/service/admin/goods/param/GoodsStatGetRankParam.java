package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.util.List;

@Data
public class GoodsStatGetRankParam {
    private List<Integer> categoryIds;
    private String type;
    private String date;
    private String goodsName;
}
