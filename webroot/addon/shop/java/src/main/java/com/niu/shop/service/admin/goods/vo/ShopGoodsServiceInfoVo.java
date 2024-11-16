package com.niu.shop.service.admin.goods.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsService视图
 */
@Data
public class ShopGoodsServiceInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer serviceId;  // 
    private Integer siteId;  // 站点id
    private String serviceName;  // 服务名称
    private String image;  // 图片
    private String desc;  // 描述

}
