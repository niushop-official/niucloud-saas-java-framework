package com.niu.shop.service.core.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.mapper.manjian.ShopManjianMapper;
import com.niu.shop.service.core.marketing.ICoreManjianStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CoreManjianStatServiceImpl implements ICoreManjianStatService {

    @Resource
    ShopManjianMapper shopManjianMapper;

    @Override
    public boolean stat(Integer siteId, Integer manjianId, Map<String, String> data) {
        Map<String, String> processedData = new HashMap<>();
        processedData.put("total_order_money", data.getOrDefault("order_money", "0"));
        processedData.put("total_order_num", data.getOrDefault("order_num", "0"));
        processedData.put("total_member_num", data.getOrDefault("member_num", "0"));
        processedData.put("total_point", data.getOrDefault("point", "0"));
        processedData.put("total_balance", data.getOrDefault("balance", "0"));
        processedData.put("total_coupon_num", data.getOrDefault("coupon_num", "0"));
        processedData.put("total_goods_num", data.getOrDefault("goods_num", "0"));

        set(siteId, manjianId, processedData);
        return true;
    }

    @Override
    public boolean set(Integer siteId, Integer manjianId, Map<String, String> data) {
        log.info("满减计算统计");
        log.info(data.toString());
        ShopManjian manjian = shopManjianMapper.selectById(manjianId);
        if (ObjectUtil.isNotEmpty(manjian)) {
            UpdateWrapper<ShopManjian> wrapper = new UpdateWrapper<>();
            wrapper.eq("manjian_id", manjianId);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                BigDecimal numValue = new BigDecimal(value);
                if (numValue.compareTo(BigDecimal.ZERO) != 0) {
                    String operation = numValue.compareTo(BigDecimal.ZERO) > 0 ? " + " : " - ";
                    BigDecimal absValue = numValue.abs();
                    String sqlExpression = key + "=" + key + operation + absValue;
                    wrapper.setSql(sqlExpression);
                }
            }
            shopManjianMapper.update(null, wrapper);
        }
        return true;
    }
}
