package com.niu.shop.service.api.order.vo;

import com.niu.core.common.domain.PageResult;
import lombok.Data;

@Data
public class ShopOrderPageVo extends PageResult<ShopOrderListVo> {
    private String mchId = "";
    private Boolean isTradeManaged = false;
}
