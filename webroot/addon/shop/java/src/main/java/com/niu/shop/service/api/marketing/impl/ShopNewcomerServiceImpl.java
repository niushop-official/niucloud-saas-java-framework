package com.niu.shop.service.api.marketing.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.newconsumer.ShopNewcomerMemberRecords;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.newconsumer.ShopNewcomerMemberRecordsMapper;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.marketing.IShopNewcomerService;
import com.niu.shop.service.api.marketing.param.ShopNewComerComponentsListParam;
import com.niu.shop.service.api.marketing.param.ShopNewcomerSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopNewComerComponentsListVo;
import com.niu.shop.service.api.marketing.vo.ShopNewComerGoodsPageVo;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerListVo;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.marketing.vo.CoreShopNewcomerConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopNewcomerServiceImpl implements IShopNewcomerService {
    private final ShopGoodsMapper shopGoodsMapper;
    private final ShopActiveMapper shopActiveMapper;
    private final ShopGoodsSkuMapper shopGoodsSkuMapper;
    private final ShopActiveGoodsMapper shopActiveGoodsMapper;
    private final ICoreShopNewcomerService coreShopNewcomerService;
    private final ShopNewcomerMemberRecordsMapper shopNewcomerMemberRecordsMapper;

    @Override
    public ShopNewComerGoodsPageVo<ShopNewcomerListVo> getGoodsPage(PageParam pageParam, ShopNewcomerSearchParam param) {
        var page = pageParam.getPage();
        var limit = pageParam.getLimit();

        var shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<ShopActiveGoods>()
                .eq(ShopActiveGoods::getSiteId, RequestUtils.siteId())
                .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType())
                .eq(ShopActiveGoods::getActiveGoodsStatus, ActiveStatusEnum.ACTIVE.getStatus());
        var shopActiveGoods = shopActiveGoodsMapper.selectList(shopActiveGoodsLambdaQueryWrapper);

        var shopActiveGoodsMap = shopActiveGoods.stream().collect(Collectors.toMap(ShopActiveGoods::getSkuId, e -> e));
        var activeGoodsIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getGoodsId);
        var activeSkuIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getSkuId);

        var shopGoodsLambdaQueryWrapper = new QueryWrapper<ShopGoods>()
                .eq("status", 1);

        if (ObjectUtil.isNotEmpty(param.getBrandId())) {
            shopGoodsLambdaQueryWrapper.eq("brand_id", param.getBrandId());
        }

        if (ObjectUtil.isNotEmpty(param.getGoodsCategory())) {
            shopGoodsLambdaQueryWrapper.eq("goods_category", param.getGoodsCategory());
        }

        if (ObjectUtil.isNotEmpty(param.getKeyword())) {
            shopGoodsLambdaQueryWrapper.and(wrapper ->
                    wrapper.like("goods_name", param.getKeyword())
                            .or()
                            .like("sub_title", param.getKeyword())
            );
        }

        if (ObjectUtil.isNotEmpty(activeGoodsIds)) {
            shopGoodsLambdaQueryWrapper.in("goods_id", activeGoodsIds);
        }

        if (ObjectUtil.isNotEmpty(param.getOrder()) && List.of("sale_num", "sale_price").contains(param.getOrder())) {
            shopGoodsLambdaQueryWrapper.orderBy(true, "asc".equals(param.getSort()), param.getOrder());
        } else {
            shopGoodsLambdaQueryWrapper.orderByDesc("sort").orderByDesc("create_time");
        }

        var iPage = shopGoodsMapper.selectPage(new Page<>(page, limit), shopGoodsLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(iPage.getRecords())) {
            return new ShopNewComerGoodsPageVo<>(iPage, Collections.emptyList());
        }

        var goodsIds = CollStreamUtil.toSet(iPage.getRecords(), ShopGoods::getGoodsId);
        var shopGoodsSkuLambdaQueryWrapper = new LambdaQueryWrapper<ShopGoodsSku>()
                .in(ShopGoodsSku::getSkuId, activeSkuIds)
                .in(ShopGoodsSku::getGoodsId, goodsIds);

        var goodsSkuMap = shopGoodsSkuMapper.selectList(shopGoodsSkuLambdaQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId));

        var list = new ArrayList<ShopNewcomerListVo>(iPage.getRecords().size());
        for (var shopGood : iPage.getRecords()) {
            var vo = new ShopNewcomerListVo();
            BeanUtils.copyProperties(shopGood, vo);
            vo.setGoodsTypeName(GoodsTypeEnum.getNameByType(shopGood.getGoodsType()));
            vo.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(RequestUtils.siteId(), shopGood.getGoodsCover()));
            vo.setGoodsCoverThumbMid(CommonUtils.thumbImageMiddle(RequestUtils.siteId(), shopGood.getGoodsCover()));

            var skuList = goodsSkuMap.getOrDefault(shopGood.getGoodsId(), Collections.emptyList());
            var skuVoList = new ArrayList<ShopNewcomerListVo.Sku>(skuList.size());
            for (var sku : skuList) {
                var skuVo = new ShopNewcomerListVo.Sku();
                BeanUtils.copyProperties(sku, skuVo);
                var activeGoodsValue = JSON.parseObject(shopActiveGoodsMap.get(sku.getSkuId()).getActiveGoodsValue());
                skuVo.setNewcomerPrice(Optional.ofNullable(activeGoodsValue.getBigDecimal("newcomer_price")).orElse(BigDecimal.ZERO));

                if (sku.getIsDefault() == 1) {
                    if (!activeSkuIds.contains(sku.getSkuId())) {
                        skuVo = skuVoList.get(0);
                    }

                    vo.setGoodsSku(skuVo);
                }

                skuVoList.add(skuVo);
            }

            vo.setSkuList(skuVoList);
            list.add(vo);
        }

        var vo = new ShopNewComerGoodsPageVo<>(iPage, list);

        var shopNewcomerMemberRecordsLambdaQueryWrapper = new LambdaQueryWrapper<ShopNewcomerMemberRecords>()
                .eq(ShopNewcomerMemberRecords::getSiteId, RequestUtils.siteId())
                .eq(ShopNewcomerMemberRecords::getMemberId, RequestUtils.memberId());
        var records = shopNewcomerMemberRecordsMapper.selectList(shopNewcomerMemberRecordsLambdaQueryWrapper);
        vo.setValidityTime(ObjectUtil.isEmpty(records) ? 0 : records.get(0).getValidityTime());
        vo.setIsJoin(ObjectUtil.isEmpty(records) ? 0 : records.get(0).getIsJoin());
        return vo;
    }

    @Override
    public ShopNewComerComponentsListVo getComponentsList(ShopNewComerComponentsListParam param) {
        var shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<ShopActiveGoods>()
                .eq(ShopActiveGoods::getSiteId, RequestUtils.siteId())
                .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType())
                .eq(ShopActiveGoods::getActiveGoodsStatus, ActiveStatusEnum.ACTIVE.getStatus());

        var activeGoods = shopActiveGoodsMapper.selectList(shopActiveGoodsLambdaQueryWrapper);
        var vo = new ShopNewComerComponentsListVo();
        if (ObjectUtil.isEmpty(activeGoods)) {
            return vo;
        }

        var activeSkuIds = CollStreamUtil.toSet(activeGoods, ShopActiveGoods::getSkuId);
        var goodsMap = CollStreamUtil.toMap(activeGoods, ShopActiveGoods::getSkuId, e -> e);

        var skuIds = (ObjectUtil.isEmpty(param.getSkuIds()) ? activeSkuIds : param.getSkuIds())
                .stream()
                .filter(activeSkuIds::contains)
                .collect(Collectors.toSet());

        var goodsSkuQueryWrapper = new MPJQueryWrapper<ShopGoodsSku>()
            .setAlias("gs")
            .leftJoin("?_shop_goods g ON gs.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
            .select("gs.*,g.goods_name,g.goods_cover")
            .eq("gs.site_id", RequestUtils.siteId())
            .in("gs.sku_id", skuIds)
            .orderByDesc("gs.sale_num")
            .orderByDesc("gs.stock");

        if (param.getLimit() != null && param.getLimit() > 0) {
            goodsSkuQueryWrapper.last("limit " + param.getLimit());
        }

        List<ShopNewComerComponentsListVo.GoodsSku> skuList = shopGoodsSkuMapper.selectJoinList(ShopNewComerComponentsListVo.GoodsSku.class, goodsSkuQueryWrapper);

        for (var goodsSku : skuList) {
            var shopActiveGoods = goodsMap.getOrDefault(goodsSku.getSkuId(), new ShopActiveGoods());
            var value = JSON.parseObject(shopActiveGoods.getActiveGoodsValue());
            goodsSku.setNewcomerPrice(Optional.ofNullable(value.getBigDecimal("newcomer_price")).orElse(BigDecimal.ZERO));
            ShopNewComerComponentsListVo.Goods goods = new ShopNewComerComponentsListVo.Goods();
            goods.setGoodsName(goodsSku.getGoodsName());
            goods.setGoodsCover(goodsSku.getGoodsCover());
            goodsSku.setGoods(goods);
        }

        vo.setGoodsList(skuList);
        var shopNewcomerMemberRecordsLambdaQueryWrapper = new LambdaQueryWrapper<ShopNewcomerMemberRecords>()
                .eq(ShopNewcomerMemberRecords::getSiteId, RequestUtils.siteId())
                .eq(ShopNewcomerMemberRecords::getMemberId, RequestUtils.memberId());
        var records = shopNewcomerMemberRecordsMapper.selectList(shopNewcomerMemberRecordsLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(records)) {
            return vo;
        }

        var memberRecords = records.get(0);
        vo.setValidityTime(memberRecords.getValidityTime());
        vo.setIsJoin(memberRecords.getIsJoin());
        return vo;
    }

    @Override
    public ShopNewcomerInfoVo getNewcomerInfo(Integer goodsId, Integer skuId) {
        var shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<ShopActiveGoods>()
                .eq(ShopActiveGoods::getSiteId, RequestUtils.siteId())
                .eq(ShopActiveGoods::getActiveGoodsStatus, ActiveStatusEnum.ACTIVE.getStatus())
                .eq(ShopActiveGoods::getActiveGoodsType, ActiveGoodsTypeEnum.GOODS_SINGLE.getType())
                .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType())
                .eq(ShopActiveGoods::getGoodsId, goodsId)
                .eq(ShopActiveGoods::getSkuId, skuId);
        var shopActiveGoods = shopActiveGoodsMapper.selectList(shopActiveGoodsLambdaQueryWrapper);
        var vo = new ShopNewcomerInfoVo();
        if (ObjectUtil.isEmpty(shopActiveGoods)) {
            return vo;
        }

        var activeGoods = shopActiveGoods.get(0);
        var shopActive = shopActiveMapper.selectById(activeGoods.getActiveId());
        var value = JSON.parseObject(activeGoods.getActiveGoodsValue());
        vo.setNewcomerPrice(Optional.ofNullable(value.getBigDecimal("newcomer_price")).orElse(BigDecimal.ZERO));
        vo.setNewcomerDesc(shopActive.getActiveDesc());
        vo.setLimitNum(Optional.ofNullable(JSON.parseObject(shopActive.getActiveValue()).getInteger("limit_num")).orElse(0));
        return vo;
    }

    @Override
    public List<ShopGoodsDetailVo.Sku> getNewcomerPriceByList(List<ShopGoodsDetailVo.Sku> skuList) {
        var list = new ArrayList<ShopGoodsDetailVo.Sku>(skuList.size());
        for (var sku : skuList) {
            var vo = new ShopGoodsDetailVo.Sku();
            BeanUtils.copyProperties(sku, vo);
            vo.setPrice(sku.getPrice());
            vo.setSalePrice(sku.getSalePrice());
            vo.setCostPrice(sku.getCostPrice());
            vo.setWeight(sku.getWeight());
            vo.setVolume(sku.getVolume());
            vo.setNewcomerPrice(sku.getNewcomerPrice());

            var shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<ShopActiveGoods>()
                    .eq(ShopActiveGoods::getSiteId, RequestUtils.siteId())
                    .eq(ShopActiveGoods::getActiveGoodsStatus, ActiveStatusEnum.ACTIVE.getStatus())
                    .eq(ShopActiveGoods::getActiveGoodsType, ActiveGoodsTypeEnum.GOODS_SINGLE.getType())
                    .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType())
                    .eq(ShopActiveGoods::getGoodsId, sku.getGoodsId())
                    .eq(ShopActiveGoods::getSkuId, sku.getSkuId());

            var activeGoods = shopActiveGoodsMapper.selectList(shopActiveGoodsLambdaQueryWrapper);
            if (ObjectUtil.isEmpty(activeGoods)) {
                vo.setNewcomerPrice(vo.getPrice().toString());
                vo.setIsNewcomer(0);
            } else {
                var goods = activeGoods.get(0);
                var value = JSON.parseObject(goods.getActiveGoodsValue());
                vo.setNewcomerPrice(value.getBigDecimal("newcomer_price").toString());
                vo.setIsNewcomer(1);
            }

            list.add(vo);
        }
        return list;
    }

    @Override
    public boolean checkIfNewcomer() {
        return coreShopNewcomerService.checkIfNewcomer(RequestUtils.siteId(), RequestUtils.memberId());
    }

    @Override
    public CoreShopNewcomerConfigVo getConfig() {
        return coreShopNewcomerService.getConfig(RequestUtils.siteId());
    }

}
