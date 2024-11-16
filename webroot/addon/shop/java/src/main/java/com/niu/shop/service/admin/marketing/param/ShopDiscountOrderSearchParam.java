package com.niu.shop.service.admin.marketing.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 限时折扣商品
 */
@Data
public class ShopDiscountOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchName;

    private String[] createTime;

    private String[] payTime;
}
