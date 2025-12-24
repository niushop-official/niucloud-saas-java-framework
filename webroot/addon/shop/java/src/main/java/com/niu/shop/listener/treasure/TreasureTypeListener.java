package com.niu.shop.listener.treasure;

import cn.hutool.json.JSONObject;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EventCallback("shop")
@Component
public class TreasureTypeListener extends CommonEventDefiner {
    @Override
    public CommonEventDefiner.CommonEventResult handleCallback(CommonEventDefiner.CommonEvent event) {
        if(event.getEventName().equals("TreasureType")){
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            Map<String,String> map = new HashMap<>();
            map.put("shop","商品");
            commonResult.setResult(map);
            return commonResult;
        }
        return null;
    }
}
