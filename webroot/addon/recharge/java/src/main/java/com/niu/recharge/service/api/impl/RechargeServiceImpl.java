package com.niu.recharge.service.api.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.recharge.entity.Recharge;
import com.niu.recharge.event.RechargeGiftContentEvent;
import com.niu.recharge.mapper.RechargeMapper;
import com.niu.recharge.service.api.IRechargeService;
import com.niu.recharge.service.api.vo.RechargeListVo;
import com.niu.recharge.service.core.ICoreRechargeConfigService;
import com.niu.recharge.service.core.vo.RechargeConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RechargeServiceImpl implements IRechargeService {
    private final RechargeMapper rechargeMapper;
    private final ICoreRechargeConfigService coreRechargeConfigService;

    @Override
    public RechargeConfigVo getRechargeConfig() {
        return coreRechargeConfigService.getRechargeConfig(RequestUtils.siteId());
    }

    @Override
    public List<RechargeListVo> getRechargeLists() {
        QueryWrapper<Recharge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("status", 1)
                .orderByDesc("sort")
                .orderByDesc("create_time");
        List<Recharge> list = rechargeMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(r -> {
                    RechargeListVo vo = new RechargeListVo();
                    BeanUtils.copyProperties(r, vo);
                    vo.setBuyPrice(Optional.ofNullable(r.getBuyPrice()).map(BigDecimal::toString).orElse(""));
                    vo.setFaceValue(Optional.ofNullable(r.getFaceValue()).map(BigDecimal::toString).orElse(""));

                    JSONObject gift = JSONUtil.parseObj(r.getGiftJson());
                    for (Map.Entry<String, Object> entry : gift.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        RechargeGiftContentEvent event = new RechargeGiftContentEvent();
                        event.setKey(key);
                        event.setValue(JSONUtil.parseObj(value).get("value"));
                        vo.setGiftContent(EventAndSubscribeOfPublisher.publishAndCallback(event));
                    }
                    return vo;
                })
                .toList();
    }

}
