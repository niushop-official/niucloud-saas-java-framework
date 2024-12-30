package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 满减活动详情
 */
@Data
public class ShopManjianInfoParam implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer goodsId;

    private Integer skuId;




}