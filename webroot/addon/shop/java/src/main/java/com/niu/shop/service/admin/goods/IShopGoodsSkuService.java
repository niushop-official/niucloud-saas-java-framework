package com.niu.shop.service.admin.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.service.admin.goods.param.ShopGoodsSkuSearchParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;

import java.util.List;

/**
 * 商品规格服务接口
 */
public interface IShopGoodsSkuService extends IService<ShopGoodsSku> {

    void insertAll(List<ShopGoodsSku> list);

    List<ShopGoodsSkuVo> list(ShopGoodsSkuSearchParam searchParam);
}
