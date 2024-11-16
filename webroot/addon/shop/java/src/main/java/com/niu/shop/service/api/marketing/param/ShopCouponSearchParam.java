package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券参数
 */
@Data
public class ShopCouponSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;

    private Integer categoryId;

    private String type;

    private String order = "";

    private String sort = "desc";

}
