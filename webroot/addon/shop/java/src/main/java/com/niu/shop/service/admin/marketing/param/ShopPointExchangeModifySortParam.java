package com.niu.shop.service.admin.marketing.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ShopPointExchangeModifySortParam {
    private Integer id;
    @NotNull
    private Integer sort;
}
