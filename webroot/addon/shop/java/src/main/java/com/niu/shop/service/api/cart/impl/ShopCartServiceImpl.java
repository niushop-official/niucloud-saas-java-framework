package com.niu.shop.service.api.cart.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
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
import com.niu.shop.service.api.cart.IShopCartService;
import com.niu.shop.service.api.cart.param.ShopCartCalculateParam;
import com.niu.shop.service.api.cart.param.ShopCartDeleteParam;
import com.niu.shop.service.api.cart.param.ShopCartParam;
import com.niu.shop.service.api.cart.vo.ShopCartCalculateResVo;
import com.niu.shop.service.api.cart.vo.ShopCartCalculateVo;
import com.niu.shop.service.api.cart.vo.ShopCartGoodsListVo;
import com.niu.shop.service.api.cart.vo.ShopCartListVo;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.marketing.IShopManjianService;
import com.niu.shop.service.api.marketing.param.ShopManjianInfoParam;
import com.niu.shop.service.api.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    IShopManjianService shopManjianService;

    @Resource
    ICoreManjianService coreManjianService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    /**
     * 购物车列表
     *
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

            //商品限购
            goodsVo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), RequestUtils.memberId(), item.getGoodsId()));

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

    @Override
    public List<ShopCartGoodsListVo> goodsList() {
        QueryWrapper<ShopCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("id");

        List<ShopCart> record = shopCartMapper.selectList(queryWrapper);

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        List<ShopCartGoodsListVo> list = new LinkedList<>();
        for (ShopCart item : record) {
            ShopCartGoodsListVo vo = new ShopCartGoodsListVo();
            BeanUtils.copyProperties(item, vo);

            ShopManjianInfoParam shopManjianInfoParam = new ShopManjianInfoParam();
            shopManjianInfoParam.setGoodsId(item.getGoodsId());
            shopManjianInfoParam.setSkuId(item.getSkuId());
            ShopManjianInfoVo manjianInfo = shopManjianService.getManjianInfo(shopManjianInfoParam);
            if (ObjectUtil.isNotEmpty(manjianInfo)) {
                vo.setManjianInfo(manjianInfo);
            }


            ShopCartGoodsListVo.Goods goodsVo = new ShopCartGoodsListVo.Goods();
            ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", item.getGoodsId()));
            BeanUtils.copyProperties(goods, goodsVo);

            //商品限购
            goodsVo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), RequestUtils.memberId(), item.getGoodsId()));

            vo.setGoods(goodsVo);

            ShopCartGoodsListVo.GoodsSku skuVo = new ShopCartGoodsListVo.GoodsSku();
            ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().eq("sku_id", item.getSkuId()));
            BeanUtils.copyProperties(sku, skuVo);

            List<ShopCartGoodsListVo.GoodsSpec> specList = CollectUtils.convert(shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", item.getGoodsId())), ShopCartGoodsListVo.GoodsSpec.class);
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

    @Override
    public ShopCartCalculateResVo calculate(ShopCartCalculateParam calculateParam) {

        ShopCartCalculateResVo res = new ShopCartCalculateResVo();
        ShopCartCalculateVo data = getGoods(calculateParam);
        coreManjianService.manjianPromotion(data, RequestUtils.siteId(), RequestUtils.memberId());
        res.setPromotionMoney(data.getPromotionMoney());
        res.setGoodsMoney(data.getGoodsMoney());
        res.setOrderMoney(data.getGoodsMoney().subtract(data.getPromotionMoney()));
        if (res.getOrderMoney().compareTo(BigDecimal.ZERO) < 0) {
            res.setOrderMoney(BigDecimal.ZERO);
        }
        List<ShopCartCalculateVo.Goods> goodsList = data.getGoodsList();
        List<ShopCartCalculateResVo.Goods> matchList = new ArrayList<>();
        for (ShopCartCalculateVo.Goods goods : goodsList) {
            ShopCartCalculateResVo.Goods matchGoods = new ShopCartCalculateResVo.Goods();
            BeanUtils.copyProperties(goods, matchGoods);
            if (ObjectUtil.isNotEmpty(goods.getPromotion()) && goods.getPromotion().containsKey("manjian")) {
                JSONObject manjian = (JSONObject) goods.getPromotion().get("manjian");
                if (manjian.containsKey("discount_array")) {
                    Map<String, String> discountArray = JSONUtil.toBean(manjian.getJSONObject("discount_array"), Map.class);
                    if (discountArray.containsKey("level") && Integer.parseInt(discountArray.get("level")) > 0) {
                        matchGoods.setLevel(Integer.parseInt(discountArray.get("level")));
                    }
                }
            }
            matchList.add(matchGoods);
        }
        res.setMatchList(matchList);

        return res;
    }

    @Override
    public ShopCartCalculateVo getGoods(ShopCartCalculateParam calculateParam) {

        Integer siteId = RequestUtils.siteId();
        Integer memberId = RequestUtils.memberId();
        List<Integer> skuIdList = calculateParam.getSkuIds().stream().map(ShopCartCalculateParam.Item::getSkuId).collect(Collectors.toList());


        MPJQueryWrapper<ShopGoodsSku> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nsgs.sku_id, nsgs.sku_name, nsgs.goods_id, nsgs.price, nsgs.stock, nsgs.member_price AS member_price_desc, nsc.id AS cart_id, nsc.num, nsg.member_discount")
                .setAlias("nsgs")
                .innerJoin("?_shop_cart nsc ON nsgs.sku_id = nsc.sku_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_shop_goods nsg ON nsgs.goods_id = nsg.goods_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("nsgs.site_id", siteId);
        queryWrapper.eq("nsc.member_id", memberId);
        queryWrapper.in("nsgs.sku_id", skuIdList);
        List<ShopCartCalculateVo.Goods> cartGoodsList = shopGoodsSkuMapper.selectJoinList(ShopCartCalculateVo.Goods.class, queryWrapper);

        ShopCartCalculateVo vo = new ShopCartCalculateVo();
        vo.setGoodsNum(0);
        vo.setGoodsMoney(BigDecimal.ZERO);
        if (ObjectUtil.isNotEmpty(cartGoodsList)) {

            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(memberId);
            List<ShopCartCalculateVo.Goods> goodsList = new ArrayList<>();
            for (ShopCartCalculateVo.Goods item : cartGoodsList) {
                Integer itemNum = calculateParam.getSkuIds().stream().filter(skuItem -> skuItem.getSkuId().equals(item.getSkuId())).findFirst().get().getNum();

                if (memberLevel != null) {
                    JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(item.getMemberPriceDesc()) ? JSONUtil.parseObj(item.getMemberPriceDesc()) : new JSONObject();
                    item.setPrice(goodsService.calculateMemberPrice(memberLevel, item.getMemberDiscount(), memberPriceConfig, item.getPrice()));
                }

                item.setGoodsMoney(item.getPrice().multiply(BigDecimal.valueOf(itemNum)));
                item.setRealGoodsMoney(item.getGoodsMoney());
                vo.setGoodsNum(vo.getGoodsNum() + itemNum);

                vo.setGoodsMoney(vo.getGoodsMoney().add(item.getPrice().multiply(BigDecimal.valueOf(itemNum))));
                goodsList.add(item);

            }
            vo.setGoodsList(goodsList);
            return vo;
        }

        return null;
    }

    /**
     * 购物车添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(ShopCartParam addParam) {
        ShopCart cart = shopCartMapper.selectOne(new QueryWrapper<ShopCart>()
                .eq("site_id", RequestUtils.siteId())
                .eq("member_id", RequestUtils.memberId())
                .eq("sku_id", addParam.getSkuId())
        );

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(RequestUtils.siteId());
        param.setGoodsId(addParam.getGoodsId());
        param.setCartNum(addParam.getNum());
        coreGoodsStatService.addStat(param);

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

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(model.getSiteId());
        param.setGoodsId(model.getGoodsId());
        param.setCartNum(editParam.getNum() - model.getNum());
        coreGoodsStatService.addStat(param);
    }

    /**
     * 购物车删除
     *
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