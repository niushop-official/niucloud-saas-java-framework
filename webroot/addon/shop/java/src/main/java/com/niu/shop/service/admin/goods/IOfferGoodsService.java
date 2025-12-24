package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.CheckGoodsParam;
import com.niu.shop.service.admin.goods.param.GoodsOutPutParam;
import com.niu.shop.service.admin.goods.vo.CheckGoodsVo;
import com.niu.shop.service.admin.goods.vo.SelectGoodsListVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsInfoVo;

import java.util.Map;

public interface IOfferGoodsService {

    Map<String, Object> getWhereInit();

    PageResult<SelectGoodsListVo> getOfferGoodsList(PageParam pageParam, GoodsOutPutParam searchParam);

    SelectGoodsListVo getOfferGoodsInfo(Integer goodsId);

    CheckGoodsVo checkGoods(CheckGoodsParam param);

}
