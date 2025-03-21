package com.niu.shop.listener.newcomer;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.shop.event.order.OrderCloseAfterEvent;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.marketing.param.ShopNewcomerActiveJoinParam;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@EventListen("shop")
@Component
public class OrderCloseAfterListener extends AbstractListener {

    @Resource
    ICoreShopNewcomerService coreShopNewcomerService;

    public void handleEvent(OrderCloseAfterEvent event) {
        ShopNewcomerActiveJoinParam param = new ShopNewcomerActiveJoinParam();
        param.setSiteId(event.getSiteId());
        param.setMemberId(event.getOrder().getMemberId());
        param.setIsJoin(0);
        param.setOrderId(event.getOrder().getOrderId());

        coreShopNewcomerService.activeJoin(param);
    }
}
