package com.niu.shop.service.api.order.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.shop.entity.delivery.ShopDeliveryLocalDelivery;
import com.niu.shop.enums.delivery.ThirdTypeEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryLocalDeliveryMapper;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryInfoVo;
import com.niu.shop.service.api.order.IOrderCreateService;
import com.niu.shop.service.core.delivery.ICoreStoreService;
import com.niu.shop.service.core.delivery.param.LatlngParam;
import com.niu.shop.service.core.delivery.vo.StoreListVo;
import com.niu.shop.service.core.order.ICoreOrderCreateService;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCouponListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderCreateServiceImpl implements IOrderCreateService {

    @Resource
    ICoreOrderCreateService coreOrderCreateService;

    @Resource
    ICoreStoreService coreStoreService;

    @Resource
    ShopDeliveryLocalDeliveryMapper shopDeliveryLocalDeliveryMapper;

    @Resource
    private ICoreConfigService coreConfigService;

    private final String ENCRYPT_STR = "*****************************";

    /**
     * 计算
     *
     * @param param
     */
    @Override
    public OrderCreateDataVo calculate(OrderCalculateParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setMemberId(RequestUtils.memberId());
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        return coreOrderCreateService.calculate(param);
    }

    /**
     * 订单创建
     *
     * @param param
     * @return
     */
    @Override
    public OrderCreateResultVo create(OrderCreateParam param) {
        return coreOrderCreateService.create(param);
    }

    /**
     * 获取订单可用优惠券
     *
     * @param orderKey
     * @return
     */
    @Override
    public List<OrderCouponListVo> getCoupon(String orderKey) {
        return coreOrderCreateService.getCoupon(orderKey);
    }

    /**
     * 获取自提点列表
     *
     * @param latlng
     */
    @Override
    public List<StoreListVo> getStore(LatlngParam latlng) {
        return coreStoreService.getStoreList(RequestUtils.siteId(), latlng);
    }

    @Override
    public ShopDeliveryLocalDeliveryInfoVo getLocal() {
        try {
            ShopDeliveryLocalDelivery model = shopDeliveryLocalDeliveryMapper.selectOne(
                    new QueryWrapper<ShopDeliveryLocalDelivery>()
                            .eq("site_id", RequestUtils.siteId())
                            .last("limit 1"));

            if (model == null) return null;

            ShopDeliveryLocalDeliveryInfoVo vo = new ShopDeliveryLocalDeliveryInfoVo();
            BeanUtils.copyProperties(model, vo);
            JSONObject config = getConfig();

            // 遍历ThirdTypeEnum中的每个条目
            for (Map.Entry<String, ThirdTypeEnum.ThirdTypeValue> entry : ThirdTypeEnum.getThirdType("").entrySet()) {
                String type = entry.getKey();
                ThirdTypeEnum.ThirdTypeValue item = entry.getValue();

                // 如果config中不包含该type，初始化
                if (!config.containsKey(type)) {
                    config.set(type, new HashMap<>());
                }

                // 获取type对应的配置JSONObject
                JSONObject typeConfigMap = config.getJSONObject(type);
                if (typeConfigMap != null) { // 检查是否为null以避免不必要的计算
                    // 遍历需要加密的参数列表
                    for (String encryptParam : item.getEncryptParams()) {
                        // 获取参数值并判断是否加密
                        String paramValue = typeConfigMap.getStr(encryptParam);
                        if (paramValue != null && !paramValue.isEmpty()) {
                            typeConfigMap.set(encryptParam, ENCRYPT_STR);
                        } else {
                            typeConfigMap.set(encryptParam, "");
                        }
                    }
                }
            }

            // 设置vo中的第三方配置信息
            vo.setThirdPartyConfig(config);
            return vo;
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject getConfig() {
        JSONObject data = coreConfigService.getConfigValue(RequestUtils.siteId(), "SHOP_THREE_PARTY_DELIVERY_CONFIG");
        for (Map.Entry<String, ThirdTypeEnum.ThirdTypeValue> entry : ThirdTypeEnum.getThirdType("").entrySet()) {
            String type = entry.getKey();
            ThirdTypeEnum.ThirdTypeValue item = entry.getValue();
            if (JSONUtil.isNull(data) || !JSONUtil.toJsonStr(data).contains(type)) {
                Map<String, String> temp = new HashMap<>();
                for (String column : item.getColumn()) {
                    temp.put(column, "");
                }
                data.put(type, temp);
            }
        }
        return data;
    }

}
