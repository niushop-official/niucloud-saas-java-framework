package com.niu.shop.service.admin.order;

import com.niu.shop.service.core.order.param.OrderConfigParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;

/**
 * 订单设置接口
 */
public interface IOrderConfigService {

    void setConfig(OrderConfigParam orderConfigParam);

    OrderConfigVo getConfig();

}
