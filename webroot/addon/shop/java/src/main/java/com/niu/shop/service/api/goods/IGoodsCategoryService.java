package com.niu.shop.service.api.goods;

import cn.hutool.json.JSONArray;
import com.niu.shop.service.api.goods.param.ShopGoodsCategorySearchParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsCategoryListVo;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;

import java.util.List;

public interface IGoodsCategoryService {

    /**
     * 获取商品分类配置
     *
     * @return
     */
    GoodsCategoryConfigVo getGoodsCategoryConfig();

    /**
     * 获取商品分类树
     *
     * @return
     */
    JSONArray tree();

    /**
     * 获取商品分类列表
     *
     * @param searchParam
     */
    List<ShopGoodsCategoryListVo> getList(ShopGoodsCategorySearchParam searchParam);
}
