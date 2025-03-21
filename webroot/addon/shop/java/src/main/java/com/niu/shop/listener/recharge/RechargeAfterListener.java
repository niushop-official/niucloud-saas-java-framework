package com.niu.shop.listener.recharge;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.common.CommonEvent;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@EventListen("shop")
@Component
@RequiredArgsConstructor
public class RechargeAfterListener extends AbstractListener {
    private final ICoreCouponMemberService coreCouponMemberService;

    public void handleEvent(CommonEvent event) {

        String eventName = event.getEventName();
        Recharge recharge = (Recharge) event.getData();
        if(eventName.equals("RechargeAfterListener"))
        {
            JSONUtil.parseArray(recharge.getGiftJson())
                    .stream()
                    .map(JSONUtil::parseObj)
                    .filter(o -> o.containsKey("coupon"))
                    .map(o -> o.getJSONObject("coupon").getJSONArray("value"))
                    .forEach(vs ->
                            vs.stream()
                                    .map(JSONUtil::parseObj)
                                    .forEach(v -> coreCouponMemberService.sendCoupon(event.getSiteId(), event.getMemberId(), v.getInt("coupon_id"), v.getInt("num")))
                    );
        }


    }
}
