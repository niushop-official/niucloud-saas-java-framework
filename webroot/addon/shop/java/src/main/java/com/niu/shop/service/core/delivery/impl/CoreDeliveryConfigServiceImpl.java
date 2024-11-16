package com.niu.shop.service.core.delivery.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.shop.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.shop.service.core.goods.ICoreGoodsCategoryService;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 商品分类实现
 */
@Service
public class CoreDeliveryConfigServiceImpl implements ICoreDeliveryConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 查询配送配置
     *
     * @return GoodsCategoryConfigVo
     */
    public List<DeliveryConfigVo> getDeliveryList(Integer siteId) {
        JSONArray config = coreConfigService.getConfigArrayValue(siteId, "SHOP_DELIVERY_CONFIG");
        List<DeliveryConfigVo> list = new LinkedList<>();
        if (config.size() == 0) {
            list.add(new DeliveryConfigVo("物流配送", 1, "express", "买家下单可以选择快递发货"));
            list.add(new DeliveryConfigVo("同城配送", 1, "local_delivery", "在配送范围内的买家可以选择同城配送"));
            list.add(new DeliveryConfigVo("门店自提", 1, "store", "买家可选择自提点提货"));
        } else {
            Map<String, String> desc = new HashMap<>();
            desc.put("express", "买家下单可以选择快递发货");
            desc.put("local_delivery", "在配送范围内的买家可以选择同城配送");
            desc.put("store", "买家可选择自提点提货");
            for (int i= 0; i < config.size(); i++) {
                DeliveryConfigVo item = JSONUtil.toBean(config.getJSONObject(i), DeliveryConfigVo.class);
                item.setDesc(desc.get(item.getKey()));
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 配送信息设置
     *
     * @param siteId
     * @param data
     */
    public void setConfig(Integer siteId, JSONArray data) {
        coreConfigService.setConfig(siteId, "SHOP_DELIVERY_CONFIG", data);
    }

    /**
     * 获取物流查询接口配置
     *
     * @param siteId
     */
    public DeliverySearchConfigVo getDeliverySearchConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "DELIVERY_INTERFACE");
        if (config.isEmpty()) return new DeliverySearchConfigVo();

        return JSONUtil.toBean(config, DeliverySearchConfigVo.class);
    }

    /**
     * 设置物流查询接口配置
     *
     * @param siteId
     * @param data
     */
    public void setDeliverySearchConfig(Integer siteId, JSONObject data) {
        coreConfigService.setConfig(siteId, "DELIVERY_INTERFACE", data);
    }
}
