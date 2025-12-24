package com.niu.shop.listener.third_addon.goods;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.service.admin.goods.IOfferGoodsService;
import com.niu.shop.service.admin.goods.impl.OfferGoodsServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@EventCallback("shop")
@Component
public class GoodsOutPutWhereInitListener extends CommonEventDefiner {

    @Resource
    private IOfferGoodsService offerGoodsService;

    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if(commonEvent.getEventName().equals("ThirdAddonOutputWhere") && commonEvent.getSource().equals("shop")){
            //定义返回对象
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            //查询数据
            Map<String, Object> res =  offerGoodsService.getWhereInit();
            //返回数据
            commonResult.setResult(res);
            return commonResult;
        }
        return null;
    }
}
