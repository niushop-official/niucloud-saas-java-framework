package com.niu.shop.listener.marketing;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.event.marketing.ActiveStartAfterEvent;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import org.springframework.stereotype.Component;

@EventListen("shop")
@Component
public class DiscountActiveStartListener extends AbstractListener {

    private IShopDiscountService shopDiscountService() {
        return SpringContext.bean(IShopDiscountService.class);
    }

    public void handleEvent(ActiveStartAfterEvent event) {
        if (event.getActive().getActiveClass().equals(ActiveClassEnum.DISCOUNT.getType())) {
            shopDiscountService().discountStartAfter(event.getActive().getActiveId());
        }
    }
}
