package com.niu.shop.service.core.goods.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.shop.service.core.goods.ICoreGoodsCategoryService;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品分类实现
 */
@Service
public class CoreGoodsCategoryServiceImpl implements ICoreGoodsCategoryService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    public GoodsCategoryConfigVo getGoodsCategoryConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "GOODS_CATEGORY_CONFIG");
        if (config.isEmpty()) return new GoodsCategoryConfigVo();
        return JSONUtil.toBean(config, GoodsCategoryConfigVo.class);
    }

    /**
     * 设置商品分类配置
     *
     * @param siteId
     * @param data
     */
    public void setGoodsCategoryConfig(Integer siteId, JSONObject data) {
        coreConfigService.setConfig(siteId, "GOODS_CATEGORY_CONFIG", data);
    }
}
