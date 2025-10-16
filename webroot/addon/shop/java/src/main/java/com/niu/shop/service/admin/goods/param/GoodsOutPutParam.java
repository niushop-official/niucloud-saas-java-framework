package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.util.List;

@Data
public class GoodsOutPutParam {
    private Integer status = 1;

    private String goodsName;

    private Integer siteId;

    private String goodsType;

    private Integer brandId;

    private Integer goodsCategory;

    private List<Integer> labelIds;
}
