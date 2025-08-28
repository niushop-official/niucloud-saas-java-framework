package com.niu.shop.listener.recharge;

import cn.hutool.json.JSONObject;
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
        JSONObject recharge = JSONUtil.parseObj(event.getData());
        if(eventName.equals("RechargeAfterListener") && recharge.containsKey("giftJson"))
        {
            recharge.getJSONObject("giftJson").getJSONObject("coupon").getJSONArray("value").stream().map(JSONUtil::parseObj)
                .forEach(v -> {
                    coreCouponMemberService.sendCoupon(event.getSiteId(), event.getMemberId(), v.getInt("coupon_id"), v.getInt("num"));
                });
        }
    }
}
