package com.niu.shop.service.api.cart.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 购物车参数
 */
@Data
public class ShopCartSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer memberId;

    private Integer goodsId;

    private Integer skuId;

    private Integer num;

    private Integer marketType;

    private Integer marketTypeId;

    private Integer status;

    private String invalidRemark;

}
