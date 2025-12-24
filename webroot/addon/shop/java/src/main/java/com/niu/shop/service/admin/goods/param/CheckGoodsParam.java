package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;

    private Integer skuId;

    private Integer num = 0;

}
