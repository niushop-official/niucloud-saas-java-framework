package com.niu.shop.listener.third_addon.order;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.service.core.third_addon.order.ICoreCreateOrderService;
import com.niu.shop.service.core.third_addon.order.vo.CoreOrderCreateResultVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建订单
 */
@Log4j2
@EventCallback("shop")
@Component
public class BackUpThirdOrderListener extends CommonEventDefiner {
    @Resource
    private ICoreCreateOrderService coreCreateOrderService;

    @Override
    public CommonEventResult handleCallback(CommonEvent event) {
        if(!event.getEventName().equals("ThirdAddonOrderBackUp")){
            return null;
        }
        Map<String, Object> data = (Map<String, Object>) event.getData();
        String source = event.getSource();

        log.info("SHOP-PINTUANBackUpThirdOrder 开始处理订单，来源: {}，数据概要: site_id: {}, source_id: {}, order_count: {}",
                source,
                data.getOrDefault("siteId", 0),
                data.getOrDefault("sourceId", 0),
                data.containsKey("orderData") ? ((List<?>) data.get("orderData")).size() : 0);
        //构建返回初始化数据
        CommonEventResult result = new CommonEventResult();
        result.setName("shop");
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<CoreOrderCreateResultVo> returnData = coreCreateOrderService.activityOrderCreate(source, data);
            log.info("订单处理流程正常结束，来源: {}", source);

            resultMap.put("data", JSONUtil.toJsonStr(returnData));
            resultMap.put("status", true);
            result.setDataMap(resultMap);
            return result;
        } catch (Exception e) {
            log.error("订单处理异常（来源: {}）: {}", source, e.getMessage(), e);
            resultMap.put("error_msg", e.getMessage());
            resultMap.put("status", false);
            result.setDataMap(resultMap);
            return result;
        }
    }
}
