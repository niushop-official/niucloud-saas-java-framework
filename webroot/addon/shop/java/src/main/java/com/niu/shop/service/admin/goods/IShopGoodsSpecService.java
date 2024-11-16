package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.service.admin.goods.param.ShopGoodsSpecParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsSpecSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSpecInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSpecListVo;

import java.util.List;

/**
 * 商品规格项/值服务接口
 */
public interface IShopGoodsSpecService {

    void insertAll(List<ShopGoodsSpec> list);
}
