package com.niu.shop.listener.third_addon.goods;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.service.admin.goods.impl.OfferGoodsServiceImpl;
import com.niu.shop.service.admin.goods.vo.ShopGoodsInfoVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@EventCallback("shop")
@Component
public class GoodsOutPutDataInfoListener extends CommonEventDefiner {

    @Resource
    OfferGoodsServiceImpl offerGoodsService;

    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if(commonEvent.getEventName().equals("ThirdAddonOutputDataInfo")){
            //定义返回对象
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            //查询数据
            JSONObject dataParam = JSONUtil.parseObj(commonEvent.getData());
            if (ObjectUtil.isNotEmpty(dataParam.getInt("id"))) {
                Integer id = dataParam.getInt("id");
                ShopGoodsInfoVo shopGoodsInfoVo =  offerGoodsService.getOfferGoodsInfo(id);
                commonResult.setResult(shopGoodsInfoVo);
            }
            //返回数据
            return commonResult;
        }
        return null;
    }
}
