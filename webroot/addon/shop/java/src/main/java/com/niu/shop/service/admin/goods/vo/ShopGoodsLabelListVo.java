package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsLabel列表视图
 */
@Data
public class ShopGoodsLabelListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;  // 标签ID
    private Integer siteId;  // 站点id
    private String labelName;  // 标签名称
    private String memo;  // 标签说明
    private Integer sort;  // 排序
    private String createTime; // 创建时间
    private String updateTime; // 更新时间

}
