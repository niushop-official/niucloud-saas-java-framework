package com.niu.shop.service.core.third_addon.order;

import com.niu.shop.service.core.third_addon.order.vo.CoreOrderCreateResultVo;

import java.util.List;
import java.util.Map;

public interface ICoreCreateOrderService {
    List<CoreOrderCreateResultVo> activityOrderCreate(String source, Map<String, Object> data);
}
