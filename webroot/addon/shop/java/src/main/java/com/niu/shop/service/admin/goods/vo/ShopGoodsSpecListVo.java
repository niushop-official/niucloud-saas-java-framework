package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsSpec列表视图
 */
@Data
public class ShopGoodsSpecListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer specId;  // 规格id
    private Integer siteId;  // 站点id
    private Integer goodsId;  // 关联商品id
    private String specName;  // 规格项名称
    private String specValues;  // 规格值名称，多个逗号隔开

}
