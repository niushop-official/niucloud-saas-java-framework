package com.niu.shop.service.core.delivery;

import com.niu.shop.service.core.order.vo.OrderCreateDataVo;

/**
 * 配送服务接口
 */
public interface ICoreExpressService {

    /**
     * 配送费用计算
     *
     * @return GoodsCategoryConfigVo
     */
    void calculate(OrderCreateDataVo vo);


}
