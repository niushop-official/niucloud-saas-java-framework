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

//    @Resource
//    private ICoreSeckillOrderCreateService coreSeckillOrderCreateService;

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
            List<CoreOrderCreateResultVo> returnData = new ArrayList<>();

            switch (source) {
                case "pintuan":
                    log.info("SHOP-PINTUANBackUpThirdOrder 开始处理拼团订单");
                    // 调用拼团订单创建服务

                    returnData = coreCreateOrderService.pintuanCreateOrder(data);
                    log.info("SHOP-PINTUANBackUpThirdOrder 拼团订单处理完成");
                    break;

                case "seckill":
                    log.info("SHOP-PINTUANBackUpThirdOrder 开始处理秒杀订单");
                    // 调用秒杀订单创建服务
//                    returnData = coreSeckillOrderCreateService.create(data);
                    log.info("SHOP-PINTUANBackUpThirdOrder 秒杀订单处理完成");
                    break;

                default:
                    log.warn("SHOP-PINTUANBackUpThirdOrder 未知订单来源: {}", source);
                    throw new CommonException("未知的订单来源: " + source);
            }

            log.info("SHOP-PINTUANBackUpThirdOrder 订单处理流程正常结束，来源: {}", source);

            resultMap.put("data", JSONUtil.toJsonStr(returnData));
            resultMap.put("status", true);
            result.setDataMap(resultMap);
            return result;
        } catch (Exception e) {
            log.error("PINTUAN-BackUpThirdOrder 订单处理异常（来源: {}）: {}", source, e.getMessage(), e);
            resultMap.put("error_msg", e.getMessage());
            resultMap.put("status", false);
            result.setDataMap(resultMap);
            return result;
        }
    }
}
