package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.service.admin.goods.param.ShopGoodsServiceSearchParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsSkuParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsSkuSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceListVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSkuInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSkuListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;

import java.util.List;

/**
 * 商品规格服务接口
 */
public interface IShopGoodsSkuService {

    void insertAll(List<ShopGoodsSku> list);

    List<ShopGoodsSkuVo> list(ShopGoodsSkuSearchParam searchParam);
}
