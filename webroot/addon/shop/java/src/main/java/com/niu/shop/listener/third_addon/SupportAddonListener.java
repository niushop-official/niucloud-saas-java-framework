package com.niu.shop.listener.third_addon;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@EventCallback("shop")
@Component
public class SupportAddonListener extends CommonEventDefiner {
    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if(commonEvent.getEventName().equals("ThirdAddonOutputSource")){
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            Map<String, String> shopArr = Map.of("key", "shop", "name", "商城");
            if (ObjectUtil.equal("pintuan", commonEvent.getSource())){
                commonResult.setResult(shopArr);
            } else if (ObjectUtil.equal("seckill", commonEvent.getSource())) {
                commonResult.setResult(shopArr);
            }
            return commonResult;
        }
        return null;
    }
}
