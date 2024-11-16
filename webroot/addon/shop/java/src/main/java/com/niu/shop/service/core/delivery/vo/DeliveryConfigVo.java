package com.niu.shop.service.core.delivery.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * ShopGoodsAttr视图
 */
@Data
@AllArgsConstructor
public class DeliveryConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer status;

    private String key;

    private String desc;
}
