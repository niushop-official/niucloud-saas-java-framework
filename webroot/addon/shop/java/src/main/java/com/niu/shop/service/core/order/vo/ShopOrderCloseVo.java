package com.niu.shop.service.core.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderCloseVo {
    private boolean isClose;
    private int closeLength;
}
