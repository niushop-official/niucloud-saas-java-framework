package com.niu.shop.service.core.goods;

import cn.hutool.json.JSONObject;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface ICoreGoodsCategoryService {

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    GoodsCategoryConfigVo getGoodsCategoryConfig(Integer siteId);

    /**
     * 设置商品分类配置
     *
     * @param siteId
     * @param data
     */
    void setGoodsCategoryConfig(Integer siteId, JSONObject data);
}
