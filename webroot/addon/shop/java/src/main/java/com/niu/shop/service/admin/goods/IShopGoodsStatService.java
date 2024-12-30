package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.GoodsStatGetRankParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsStatDateParam;
import com.niu.shop.service.admin.goods.vo.GoodsStatRankInfo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsStatBasicVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsStatTrendVo;

import java.util.Map;

/**
 * 商品数据统计服务接口
 */
public interface IShopGoodsStatService {
    void syncStatGoods();

    ShopGoodsStatBasicVo getBasic(ShopGoodsStatDateParam param);

    ShopGoodsStatTrendVo getTrend(ShopGoodsStatDateParam param);

    PageResult<GoodsStatRankInfo> getRank(PageParam pageParam, GoodsStatGetRankParam param);

    Map<String, String> getType();
}
