package com.niu.shop.listener.recharge;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.event.common.CommonEventDefiner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EventListen("shop")
@EventCallback("shop")
public class GiftContentListener extends CommonEventDefiner {

    @Override
    public CommonEventDefiner.CommonEventResult handleCallback(CommonEventDefiner.CommonEvent event) {

        if(event.getEventName().equals("RechargeGiftContent"))
        {
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            JSONObject data = JSONUtil.parseObj(event.getData());
            if (!data.containsKey("coupon")) {
                return commonResult;
            }

            JSONObject valuesJson = JSONUtil.parseObj(data.get("coupon"));
            JSONArray values = valuesJson.getJSONArray("value");
            int count = values.stream().mapToInt(o -> JSONUtil.parseObj(o).getInt("num")).sum();

            List<String> detail = values.stream()
                    .map(JSONUtil::parseObj)
                    .map(o -> o.get("num") + "张" + o.get("title"))
                    .toList();

            RechargeGiftContentVo result = new RechargeGiftContentVo();
            result.setDetail(detail);
            result.setInfo("优惠券*" + count);
            result.setLabel("优惠券");
            commonResult.setResult(result);
            return commonResult;
        }
        return null;

    }
}
