package com.niu.shop.service.core.delivery.electronic.sheet.engine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElectronicSheetFactory {

    public static ElectronicSheetEngine getEngine(String interfaceType, String id, String key) {
        if (interfaceType.equals("kdbird")) {
            return new KdBirdsEngine(id, key);
        }

        log.error("不支持的面单服务商");
        throw new RuntimeException("未配置电子面单接口");
    }
}
