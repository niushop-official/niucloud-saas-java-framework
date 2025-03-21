package com.niu.shop.listener.diy;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.event.diy.DiyFormDelBeforeEventDefiner;
import com.niu.shop.service.admin.diy.IShopDiyFormService;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EventListen("shop")
@EventCallback("shop")
public class DiyFormDelBeforeListener extends DiyFormDelBeforeEventDefiner {

    private IShopDiyFormService shopDiyFormService() {
        return SpringContext.bean(IShopDiyFormService.class);
    }
    @Override
    public DiyFromDelBeforeResult handleCallback(DiyFormDelBeforeEvent diyFormDelBeforeEvent) {
        DiyFromDelBeforeResult result = new DiyFromDelBeforeResult();
        if(shopDiyFormService().diyFromDelBefore(diyFormDelBeforeEvent.getFormId())){
            result.setAllowOperate(true);
        }else{
            result.setAllowOperate(false);
        }
        return result;
    }
}
