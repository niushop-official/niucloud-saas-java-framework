package com.niu.shop.listener.treasure;

import cn.hutool.json.JSONObject;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import org.springframework.stereotype.Component;

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

        return null;
    }
}
