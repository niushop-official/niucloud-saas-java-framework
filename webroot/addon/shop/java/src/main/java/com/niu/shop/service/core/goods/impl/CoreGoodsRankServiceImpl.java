package com.niu.shop.service.core.goods.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.shop.service.core.goods.ICoreGoodsRankService;
import com.niu.shop.service.core.goods.param.ShopGoodsRankConfigParam;
import com.niu.shop.service.core.goods.vo.ShopGoodsRankConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreGoodsRankServiceImpl implements ICoreGoodsRankService {
    private static final String KEY = "GOODS_RANK_CONFIG";
    private final ICoreConfigService coreConfigService;

    @Override
    public void setGoodsRankConfig(ShopGoodsRankConfigParam param) {
        coreConfigService.setConfig(param.getSiteId(), KEY, JSONUtil.parseObj(param));
    }

    @Override
    public ShopGoodsRankConfigVo getGoodsRankConfig(Integer siteId) {
        CoreSysConfigVo config = coreConfigService.getConfig(siteId, KEY);
        ShopGoodsRankConfigVo vo = JSONUtil.toBean(config.getValue(), ShopGoodsRankConfigVo.class);
        vo = Optional.ofNullable(vo).orElseGet(ShopGoodsRankConfigVo::new);

        vo.setRankImages(Optional.ofNullable(vo.getRankImages()).orElse("addon/shop/rank/rank_images.jpg"));
        vo.setNoColor(Optional.ofNullable(vo.getNoColor()).orElse("#FFFCF5"));
        vo.setSelectColor(Optional.ofNullable(vo.getSelectColor()).orElse("#FF4142"));
        vo.setSelectBgColorStart(Optional.ofNullable(vo.getSelectBgColorStart()).orElse("#FFFFFF"));
        vo.setSelectBgColorEnd(Optional.ofNullable(vo.getSelectBgColorEnd()).orElse("#FFEBD7"));
        return vo;
    }
}
