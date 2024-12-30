package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

@Data
public class ShopGoodsRankSelectPageInfo {
    private Integer rankId;
    private Integer siteId;
    private String name;
    private String rankType;
    private String goodsSource;
    private String ruleType;
    private String goodsJson;
    private String categoryIds;
    private String brandIds;
    private String labelIds;
    private Integer sort;
    private Integer status;
    private Long createTime;
    private Long updateTime;
    private String rankTypeName;
    private String goodsSourceName;
    private String ruleTypeName;
    private long showGoodsNum;
}
