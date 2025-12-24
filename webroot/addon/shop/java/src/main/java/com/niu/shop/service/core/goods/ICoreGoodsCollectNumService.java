package com.niu.shop.service.core.goods;

import com.niu.core.entity.pay.PayRefund;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import io.swagger.v3.oas.models.security.SecurityScheme;

public interface ICoreGoodsCollectNumService {

    void inc(Integer goodsId, Integer collectNum);

    void dec(Integer goodsId, Integer collectNum);
}
