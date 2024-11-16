package com.niu.shop.service.core.delivery;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 配送服务接口
 */
public interface ICoreDeliveryConfigService {

    /**
     * 查询配送配置
     *
     * @return GoodsCategoryConfigVo
     */
    List<DeliveryConfigVo> getDeliveryList(Integer siteId);

    /**
     * 配送信息设置
     *
     * @param siteId
     * @param data
     */
    void setConfig(Integer siteId, JSONArray data);

    /**
     * 获取物流查询接口配置
     *
     * @param siteId
     */
    DeliverySearchConfigVo getDeliverySearchConfig(Integer siteId);

    /**
     * 设置物流查询接口配置
     *
     * @param siteId
     * @param data
     */
    void setDeliverySearchConfig(Integer siteId, JSONObject data);
}
