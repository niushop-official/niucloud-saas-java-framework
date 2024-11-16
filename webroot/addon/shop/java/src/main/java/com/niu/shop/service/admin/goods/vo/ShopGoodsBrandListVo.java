package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsBrand列表视图
 */
@Data
public class ShopGoodsBrandListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer brandId;  // 品牌ID
    private Integer siteId;  // 站点id
    private String brandName;  // 品牌名称
    private String logo;  // 品牌logo
    private String desc;  // 品牌介绍
    private Integer sort;  // 排序
    private String createTime; // 创建时间
    private String updateTime; // 更新时间

}
