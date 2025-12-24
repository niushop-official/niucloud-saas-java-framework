package com.niu.shop.listener.third_addon.goods;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.service.admin.goods.IOfferGoodsService;
import com.niu.shop.service.admin.goods.vo.SelectGoodsListVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsInfoVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@EventCallback("shop")
@Component
public class GoodsOutPutDataInfoListener extends CommonEventDefiner {

    @Resource
    private IOfferGoodsService offerGoodsService;

    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if(commonEvent.getEventName().equals("ThirdAddonOutputDataInfo") && "shop".equals(commonEvent.getSource())){
            //定义返回对象
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            //查询数据
            if (ObjectUtil.isNotEmpty(commonEvent.getData())) {
                Integer goodsId = Integer.parseInt(commonEvent.getData().toString());
                SelectGoodsListVo shopGoodsInfoVo =  offerGoodsService.getOfferGoodsInfo(goodsId);
                commonResult.setResult(shopGoodsInfoVo);
            }
            //返回数据
            return commonResult;
        }
        return null;
    }
}
