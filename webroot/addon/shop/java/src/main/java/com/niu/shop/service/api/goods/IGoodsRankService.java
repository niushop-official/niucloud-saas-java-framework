package com.niu.shop.service.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.goods.ShopGoodsRank;
import com.niu.shop.service.admin.goods.param.ShopGoodsRankSearchParam;
import com.niu.shop.service.api.goods.param.GoodsRankComponentParam;
import com.niu.shop.service.api.goods.param.ShopGoodsRankGoodsPageParam;
import com.niu.shop.service.api.goods.vo.GoodsRankComponentVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsRankGoodsPageVo;
import com.niu.shop.service.core.goods.vo.ShopGoodsRankConfigVo;

import java.util.List;

public interface IGoodsRankService {
    ShopGoodsRankConfigVo getGoodsRankConfig();

    List<ShopGoodsRank> page(ShopGoodsRankSearchParam searchParam);

    ShopGoodsRankGoodsPageVo getGoodsPage(PageParam pageParam, ShopGoodsRankGoodsPageParam param);

    GoodsRankComponentVo getRankComponents(PageParam pageParam, GoodsRankComponentParam param);
}
