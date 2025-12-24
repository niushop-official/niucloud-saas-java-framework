package com.niu.shop.service.core.delivery;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;

import java.util.List;

/**
 * 配送服务接口
 */
public interface ICoreLocalDeliveryService {

    /**
     * 配送费用计算
     *
     * @return GoodsCategoryConfigVo
     */
    void calculate(OrderCreateDataVo vo);


    JSONObject getConfig(Integer siteId);

    void setConfig(JSONObject config, Integer siteId);
}
