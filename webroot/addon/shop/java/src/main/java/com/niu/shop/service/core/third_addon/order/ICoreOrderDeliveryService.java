package com.niu.shop.service.core.third_addon.order;

import com.niu.shop.service.core.third_addon.order.param.CalculateDeliveryMoneyParam;

import java.math.BigDecimal;

public interface ICoreOrderDeliveryService {
    /**
     * 计算运费
     * @param vo
     * @return
     */
    BigDecimal calculate(CalculateDeliveryMoneyParam param);
}
