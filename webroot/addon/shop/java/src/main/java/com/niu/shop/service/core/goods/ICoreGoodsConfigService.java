package com.niu.shop.service.core.goods;

import cn.hutool.json.JSONObject;
import com.niu.shop.service.admin.goods.param.ShopGoodsVerifySkuNoParam;
import com.niu.shop.service.core.goods.param.SortConfigParam;
import com.niu.shop.service.core.goods.param.ShopConfigParam;
import com.niu.shop.service.core.goods.vo.SortConfigVo;

/**
 * 商品管理中商品设置
 */
public interface ICoreGoodsConfigService {

    /**
     * 获取商品搜索配置
     */
    JSONObject getSearchConfig();

    /**
     * 设置商品搜索配置
     * @param shopConfigParam
     */
    void setSearchConfig(ShopConfigParam shopConfigParam);

    /**
     * 获取商品编码唯一值配置
     */
    JSONObject getUniqueConfig();

    /**
     * 设置商品编码唯一值配置
     * @param shopConfigParam
     */
    void setUniqueConfig(ShopConfigParam shopConfigParam);

    /**
     * 获取商品排序配置
     */
    SortConfigVo getSortConfig();



    /**
     * 设置商品排序配置
     * @param sortConfigParam
     */
    void setSortConfig(SortConfigParam sortConfigParam);

    Boolean verifySkuNo(ShopGoodsVerifySkuNoParam verifySkuNoParam);
}
