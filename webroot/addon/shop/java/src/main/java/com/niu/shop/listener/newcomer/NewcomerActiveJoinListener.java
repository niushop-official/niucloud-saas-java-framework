package com.niu.shop.listener.newcomer;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.shop.event.order.OrderCreateAfterEvent;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.marketing.param.ShopNewcomerActiveJoinParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@EventListen("shop")
@Component
public class NewcomerActiveJoinListener extends AbstractListener {

    @Resource
    ICoreShopNewcomerService coreShopNewcomerService;

    public void handleEvent(OrderCreateAfterEvent event) {
        ShopNewcomerActiveJoinParam param = new ShopNewcomerActiveJoinParam();
        param.setSiteId(event.getSiteId());
        param.setMemberId(event.getOrder().getMemberId());
        param.setIsJoin(1);
        param.setOrderId(event.getOrder().getOrderId());

        coreShopNewcomerService.activeJoin(param);
    }
}
