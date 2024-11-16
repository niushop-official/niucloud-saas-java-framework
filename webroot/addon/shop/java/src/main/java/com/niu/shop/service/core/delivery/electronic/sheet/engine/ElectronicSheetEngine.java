package com.niu.shop.service.core.delivery.electronic.sheet.engine;

public interface ElectronicSheetEngine {

    /**
     * 申请电子面单
     */
    ApplyResult apply(String cacheKey, ApplyParam param);
}
