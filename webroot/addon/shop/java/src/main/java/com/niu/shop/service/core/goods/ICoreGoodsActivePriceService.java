package com.niu.shop.service.core.goods;


import cn.hutool.json.JSONObject;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.shop.service.core.goods.param.GetShowPriceParam;
import com.niu.shop.service.core.goods.vo.ShowPriceVo;

import java.math.BigDecimal;

public interface ICoreGoodsActivePriceService {

    /**
     * 获取商品展示价格
     * @param param
     * @return
     */
    ShowPriceVo getShowPrice(GetShowPriceParam param);

    /**
     * 计算会员价格
     * @param memberLevel
     * @param calculateType
     * @param config
     * @param price
     * @return
     */
    BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price);
}
