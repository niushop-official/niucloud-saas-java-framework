package com.niu.shop.listener.third_addon.goods;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.service.admin.goods.IOfferGoodsService;
import com.niu.shop.service.admin.goods.impl.OfferGoodsServiceImpl;
import com.niu.shop.service.admin.goods.param.GoodsOutPutParam;
import com.niu.shop.service.admin.goods.vo.SelectGoodsListVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@EventCallback("shop")
@Component
public class GoodsOutPutDataListener extends CommonEventDefiner {

    @Resource
    private IOfferGoodsService offerGoodsService;

    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if (commonEvent.getEventName().equals("ThirdAddonOutputData") && "shop".equals(commonEvent.getSource())) {
            //获取传过来的条件数据data
            JSONObject data = JSONUtil.parseObj(commonEvent.getData());
            //定义返回对象
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            //获取page和limit数据
            PageParam pageParam = data.getBean("pageParam", PageParam.class);
            if (ObjectUtil.isNull(pageParam)) {
                pageParam = new PageParam();
                pageParam.setPage(1);
                pageParam.setLimit(10);
            }
            GoodsOutPutParam searchParam = JSONUtil.toBean(data, GoodsOutPutParam.class);
            searchParam.setSiteId(commonEvent.getSiteId());
            PageResult<SelectGoodsListVo> shopSelectGoodsListVo = offerGoodsService.getOfferGoodsList(pageParam, searchParam);
            commonResult.setResult(shopSelectGoodsListVo);
            return commonResult;
        }
        return null;
    }
}
