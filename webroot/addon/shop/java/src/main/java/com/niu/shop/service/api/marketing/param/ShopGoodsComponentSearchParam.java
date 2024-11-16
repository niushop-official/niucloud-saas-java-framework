package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShopGoodsComponentSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer num;
    private String names;
    private String ids;
    private String order;
    private String sort;
    private Integer status;
}
