package com.niu.shop.service.api.marketing.vo;

import com.niu.shop.entity.goods.ShopGoodsSku;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShopNewcomerPriceVo extends ShopGoodsSku {
    private BigDecimal newcomerPrice;
    private Integer isNewcomer;
}
