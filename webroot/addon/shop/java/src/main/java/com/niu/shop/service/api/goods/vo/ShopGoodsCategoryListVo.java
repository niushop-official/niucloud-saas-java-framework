package com.niu.shop.service.api.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品参数
 */
@Data
public class ShopGoodsCategoryListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer categoryId;  // 商品分类id
    private Integer siteId;  // 站点id
    private String categoryName;  // 分类名称
    private String image;  // 分类图片
    private Integer level;  // 层级
    private Integer pid;  // 上级分类id
    private String categoryFullName;  // 组装分类名称

}
