package com.niu.shop.service.admin.delivery;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.shop.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 配送方式相关接口
 */
public interface IDeliveryService {

    /**
     * 配送方式
     */
    List<DeliveryConfigVo> getDeliveryList();

    /**
     * 配送信息设置
     *
     * @param data
     */
    void setConfig(JSONArray data);

    /**
     * 获取物流查询接口配置
     */
    DeliverySearchConfigVo getDeliverySearchConfig();

    /**
     * 设置物流查询接口配置
     * @param data
     */
    void setDeliverySearchConfig(DeliverySearchConfigParam data);
}
