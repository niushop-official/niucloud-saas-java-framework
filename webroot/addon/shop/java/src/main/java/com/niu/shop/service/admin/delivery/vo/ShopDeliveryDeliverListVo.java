package com.niu.shop.service.admin.delivery.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ShopDeliveryDeliver列表视图
 */
@Data
public class ShopDeliveryDeliverListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer deliverId;  // 配送员id
    private Integer siteId;  // 站点id
    private String deliverName;  // 配送员名称
    private String deliverMobile;  // 配送员手机号
    private String createTime; // 创建时间
    private Integer modifyTime;  // 修改时间
    private Integer storeId;  // 门店id

}
