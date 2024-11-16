package com.niu.shop.service.api.goods;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.shop.service.api.goods.param.ShopGoodsComponentsSearchParam;
import com.niu.shop.service.api.goods.param.ShopGoodsDetailParam;
import com.niu.shop.service.api.goods.param.ShopGoodsSearchParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;

import java.math.BigDecimal;
import java.util.List;

public interface IGoodsService {

    /**
     * 查询商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<ShopGoodsListVo> page(PageParam pageParam, ShopGoodsSearchParam searchParam);

    /**
     * 获取商品详情
     *
     * @param param
     * @return
     */
    ShopGoodsDetailVo detail(ShopGoodsDetailParam param);

    /**
     * 获取商品规格信息，切换规格
     *
     * @param skuId
     * @return
     */
    ShopGoodsSkuVo getSku(Integer skuId);

    /**
     * 计算会员价
     *
     * @param memberLevel
     * @param calculateType
     * @param config
     * @param price
     * @return
     */
    BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price);

    /**
     * 获取商品列表供组件调用
     * @param param
     */
    List<ShopGoodsListVo> getGoodsComponents(ShopGoodsComponentsSearchParam param);
}
