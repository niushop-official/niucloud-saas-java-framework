package com.niu.shop.service.admin.goods.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsLabel视图
 */
@Data
public class ShopGoodsLabelInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;  // 标签ID
    private Integer siteId;  // 站点id
    private String labelName;  // 标签名称
    private String memo;  // 标签说明
    private Integer sort;  // 排序

}
