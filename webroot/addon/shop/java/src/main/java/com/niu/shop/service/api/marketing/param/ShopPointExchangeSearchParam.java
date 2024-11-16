package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopPointExchangeSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String names;
    private Integer status;
    private String sort;
    private String order;
    private List<String> createTime;
}
