package com.niu.shop.listener.recharge;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.recharge.event.RechargeGiftContentEvent;
import com.niu.recharge.service.core.vo.RechargeGiftContentVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EventCallback("shop")
public class GiftContentListener extends CallbackListener<RechargeGiftContentEvent> {

    @Override
    public RechargeGiftContentVo handleCallback(RechargeGiftContentEvent event) {
        if (!"coupon".equals(event.getKey())) {
            return new RechargeGiftContentVo();
        }

        JSONArray values = JSONUtil.parseArray(event.getValue());
        int count = values.stream().mapToInt(o -> JSONUtil.parseObj(o).getInt("num")).sum();

        List<String> detail = values.stream()
                .map(JSONUtil::parseObj)
                .map(o -> o.get("num") + "张" + o.get("title"))
                .toList();

        RechargeGiftContentVo result = new RechargeGiftContentVo();
        result.setDetail(detail);
        result.setInfo("优惠券*" + count);
        result.setLabel("优惠券");

        return result;
    }
}
