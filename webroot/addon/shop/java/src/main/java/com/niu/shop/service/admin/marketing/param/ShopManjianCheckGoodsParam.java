package com.niu.shop.service.admin.marketing.param;

import lombok.Data;

import java.util.List;

@Data
public class ShopManjianCheckGoodsParam {
    private String goodsType;
    private String startTime;
    private String endTime;
    private List<Integer> goodsIds;
    private Integer manjianId;
}
