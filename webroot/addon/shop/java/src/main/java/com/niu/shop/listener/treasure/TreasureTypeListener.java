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
/*    @Override
    public TreasureTypeEventResult handleCallback(TreasureTypeEvent event) {
        TreasureTypeEventResult result = new TreasureTypeEventResult();
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("shop", "商品");
        result.setResult(jsonObject);
        return result;
    }*/
    @Override
    public CommonEventDefiner.CommonEventResult handleCallback(CommonEventDefiner.CommonEvent event) {
        if(event.getEventName().equals("TreasureDataTypeList")){
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            Map<String,String> map = new HashMap<>();
            map.put("shop","商品");
            commonResult.setResult(map);
            return commonResult;
        }
        return null;
    }
}
