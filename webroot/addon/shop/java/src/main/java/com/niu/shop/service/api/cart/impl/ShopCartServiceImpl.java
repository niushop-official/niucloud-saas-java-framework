package com.niu.shop.service.api.cart.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.shop.entity.cart.ShopCart;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.mapper.cart.ShopCartMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.goods.ShopGoodsSpecMapper;
import com.niu.shop.service.api.cart.param.ShopCartDeleteParam;
import com.niu.shop.service.api.cart.param.ShopCartParam;
import com.niu.shop.service.api.cart.IShopCartService;
import com.niu.shop.service.api.cart.vo.ShopCartListVo;
import com.niu.shop.service.api.goods.IGoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 购物车实现
 */
@Service
public class ShopCartServiceImpl implements IShopCartService {
        
    @Resource
    ShopCartMapper shopCartMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSpecMapper shopGoodsSpecMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    IGoodsService goodsService;

    /**
     * 购物车列表
     * @return List<ShopCartListVo>
     */
    @Override
    public List<ShopCartListVo> list() {
        QueryWrapper<ShopCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("id");

        List<ShopCart> record = shopCartMapper.selectList(queryWrapper);

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        List<ShopCartListVo> list = new LinkedList<>();
        for (ShopCart item : record) {
            ShopCartListVo vo = new ShopCartListVo();
            BeanUtils.copyProperties(item, vo);

            ShopCartListVo.Goods goodsVo = new ShopCartListVo.Goods();
            ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", item.getGoodsId()));
            BeanUtils.copyProperties(goods, goodsVo);
            vo.setGoods(goodsVo);

            ShopCartListVo.GoodsSku skuVo = new ShopCartListVo.GoodsSku();
            ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().eq("sku_id", item.getSkuId()));
            BeanUtils.copyProperties(sku, skuVo);

            List<ShopCartListVo.GoodsSpec> specList = CollectUtils.convert(shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", item.getGoodsId())), ShopCartListVo.GoodsSpec.class);
            vo.setGoodsSpec(specList);

            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
                skuVo.setMemberPrice(goodsService.calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, sku.getPrice()));
            } else {
                skuVo.setMemberPrice(sku.getPrice());
            }

            vo.setGoodsSku(skuVo);

            list.add(vo);
        }
        return list;
    }

    /**
     * 购物车添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopCartParam addParam) {
        ShopCart cart = shopCartMapper.selectOne(new QueryWrapper<ShopCart>()
                .eq("site_id", RequestUtils.siteId())
                .eq("member_id", RequestUtils.memberId())
                .eq("sku_id", addParam.getSkuId())
        );
        if (cart != null) {
            cart.setNum(cart.getNum() + addParam.getNum());
            shopCartMapper.updateById(cart);
        } else {
            ShopCart model = new ShopCart();
            BeanUtils.copyProperties(addParam, model);
            model.setSiteId(RequestUtils.siteId());
            model.setMemberId(RequestUtils.memberId());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            shopCartMapper.insert(model);
        }
    }

    /**
     * 购物车编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(ShopCartParam editParam) {
        ShopCart model = shopCartMapper.selectOne(
                new QueryWrapper<ShopCart>()
                    .eq("id", editParam.getId())
                    .eq("site_id", RequestUtils.siteId())
                    .eq("member_id", RequestUtils.memberId()));

        Assert.notNull(model, "数据不存在!");
        model.setNum(editParam.getNum());

        if (!model.getSkuId().equals(editParam.getSkuId())) {
            model.setSkuId(editParam.getSkuId());

            shopCartMapper.delete(new QueryWrapper<ShopCart>()
                    .eq("sku_id", editParam.getSkuId())
                    .eq("site_id", RequestUtils.siteId())
                    .eq("member_id", RequestUtils.memberId()));
        }

        shopCartMapper.updateById(model);
    }

    /**
     * 购物车删除
     * @param param 主键ID
     */
    @Override
    public void del(ShopCartDeleteParam param) {
        QueryWrapper queryWrapper = new QueryWrapper<ShopCart>()
                .eq("site_id", RequestUtils.siteId())
                .eq("member_id", RequestUtils.memberId());

        if (param.getIds() instanceof Integer) {
            queryWrapper.eq("id", param.getIds());
        } else if (param.getIds() instanceof ArrayList) {
            Integer[] ids = ((ArrayList<?>) param.getIds()).toArray(new Integer[0]);
            queryWrapper.in("id", ids);
        }

        shopCartMapper.delete(queryWrapper);
    }

    /**
     * 清空购物车
     */
    @Override
    public void clear() {
        shopCartMapper.delete(new QueryWrapper<ShopCart>().eq("site_id", RequestUtils.siteId()).eq("member_id", RequestUtils.memberId()));
    }

    /**
     * 获取购物车数量
     *
     * @param goodsId
     * @return
     */
    @Override
    public Integer getSum(Integer goodsId) {
        QueryWrapper<ShopCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (goodsId > 0) queryWrapper.eq("goods_id", goodsId);
        queryWrapper.select("SUM(num) as num");
        ShopCart card = shopCartMapper.selectOne(queryWrapper);
        if (card == null) return 0;
        return card.getNum();
    }
}
