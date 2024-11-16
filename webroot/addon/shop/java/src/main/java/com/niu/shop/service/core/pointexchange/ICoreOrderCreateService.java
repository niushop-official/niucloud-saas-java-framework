package com.niu.shop.service.core.pointexchange;

import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.pointexchange.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;

public interface ICoreOrderCreateService {

    /**
     * 计算
     *
     * @param param
     */
    OrderCreateDataVo calculate(OrderCalculateParam param);

    /**
     * 创建
     *
     * @param param
     * @return
     */
    OrderCreateResultVo create(OrderCreateParam param);
}
