package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.shop.entity.delivery.ShopDeliveryLocalDelivery;
import com.niu.shop.enums.delivery.ThirdTypeEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryLocalDeliveryMapper;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliveryParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliverySearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryLocalDeliveryService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryListVo;
import com.niu.shop.service.admin.delivery.vo.ThirdTypeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 同城配送实现
 */
@Service
public class ShopDeliveryLocalDeliveryServiceImpl implements IShopDeliveryLocalDeliveryService {

    @Resource
    ShopDeliveryLocalDeliveryMapper shopDeliveryLocalDeliveryMapper;

    @Resource
    private ICoreConfigService coreConfigService;

    private final String ENCRYPT_STR = "*****************************";

    /**
     * 同城配送详情
     *
     * @return ShopDeliveryLocalDeliveryInfoVo
     */
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


    /**
     * 同城配送设置
     *
     * @param param 参数
     */
    @Override
    public void setLocal(ShopDeliveryLocalDeliveryParam param) {
        shopDeliveryLocalDeliveryMapper.delete(new QueryWrapper<ShopDeliveryLocalDelivery>().eq("site_id", RequestUtils.siteId()));

        ShopDeliveryLocalDelivery model = new ShopDeliveryLocalDelivery();
        BeanUtils.copyProperties(param, model);
        model.setSiteId(RequestUtils.siteId());
        model.setDeliveryType(param.getDeliveryType().toString());
        model.setCenter(param.getCenter().toString());
        model.setArea(param.getArea().toString());
        model.setDeliveryTime(param.getDeliveryTime().toString());
        shopDeliveryLocalDeliveryMapper.insert(model);
        if (param.getDeliveryType().toString().contains("third")) {
            setThirdPartyConfig(param.getThirdPartyConfig());
        }
    }

    private void setThirdPartyConfig(Map<String, Object> data) {
        if (data.isEmpty()) {
            return;
        }
        JSONObject config = getConfig();
        if (data.containsKey("default")) {
            config.set("default", data.get("default"));
        }
        data.remove("default");

        for (Map.Entry<String, Object> typeEntry : data.entrySet()) {
            String type = typeEntry.getKey();
            Map<String, Object> item = (Map<String, Object>) typeEntry.getValue();

            if (!config.containsKey(type)) {
                config.put(type, new HashMap<String, Object>());
            }
            Map<String, Object> typeConfig = (Map<String, Object>) config.get(type);

            for (Map.Entry<String, Object> columnEntry : item.entrySet()) {
                String column = columnEntry.getKey();
                Object v = columnEntry.getValue();

                if (v == null || "".equals(v) || ENCRYPT_STR.equals(v)) {
                    // 如果值为空或加密字符串，保留原配置（不更新）
                    if (!typeConfig.containsKey(column)) {
                        typeConfig.put(column, ObjectUtil.isNotEmpty(v) ? v : typeConfig.get(column));
                    }
                } else {
                    // 否则使用新值
                    typeConfig.put(column, v);
                }
            }
            config.put(type, typeConfig);
        }
        setConfig(config);
    }

    @Override
    public List<ThirdTypeVo> getThirdTypeConfig() {
        Map<String, ThirdTypeEnum.ThirdTypeValue> thirdTypeMap = ThirdTypeEnum.getThirdType("");
        return thirdTypeMap.entrySet().stream()
                .map(entry -> {
                    ThirdTypeVo thirdTypeVo = new ThirdTypeVo();
                    thirdTypeVo.setType(entry.getKey());
                    thirdTypeVo.setName(entry.getValue().getName());
                    return thirdTypeVo;
                })
                .collect(Collectors.toList());
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

    public void setConfig(JSONObject data) {
        coreConfigService.setConfig(RequestUtils.siteId(), "SHOP_THREE_PARTY_DELIVERY_CONFIG", data);
    }

}
