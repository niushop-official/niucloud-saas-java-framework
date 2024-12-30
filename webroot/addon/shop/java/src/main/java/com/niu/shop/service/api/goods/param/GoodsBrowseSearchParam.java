package com.niu.shop.service.api.goods.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsBrowseSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String[] date;
}
