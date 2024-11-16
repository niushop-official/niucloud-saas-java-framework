package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsService列表视图
 */
@Data
public class ShopGoodsServiceListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer serviceId;  // 
    private Integer siteId;  // 站点id
    private String serviceName;  // 服务名称
    private String image;  // 图片
    private String desc;  // 描述
    private String createTime; // 创建时间
    private String updateTime; // 更新时间

}
