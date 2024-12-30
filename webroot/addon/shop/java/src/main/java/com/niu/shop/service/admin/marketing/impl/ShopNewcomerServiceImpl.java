package com.niu.shop.service.admin.marketing.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.marketing.IShopNewcomerService;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectPageParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectSkuParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSetConfigParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerShopGoodsVo;
import com.niu.shop.service.admin.marketing.vo.ShopNewComerSelectPageVo;
import com.niu.shop.service.admin.marketing.vo.ShopNewcomerConfigVo;
import com.niu.shop.service.admin.marketing.vo.ShopNewcomerSelectSkuVo;
import com.niu.shop.service.core.marketing.ICoreActiveService;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.marketing.model.ShopManjianGoodsData;
import com.niu.shop.service.core.marketing.model.ShopNewcomerActiveValue;
import com.niu.shop.service.core.marketing.vo.CoreShopNewcomerConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 新人专享会员参与记录实现
 */
@Service
@RequiredArgsConstructor
public class ShopNewcomerServiceImpl implements IShopNewcomerService {
    private final ShopGoodsMapper shopGoodsMapper;
    private final ShopActiveMapper shopActiveMapper;
    private final ICoreActiveService coreActiveService;
    private final ICoreUploadService coreUploadService;
    private final ShopGoodsSkuMapper shopGoodsSkuMapper;
    private final ShopActiveGoodsMapper shopActiveGoodsMapper;
    private final ICoreShopNewcomerService coreShopNewcomerService;

    @Override
    @Transactional
    public void setConfig(ShopNewcomerSetConfigParam param) {
        List<ShopManjianGoodsData> goodsDataList = JSONArray.parseArray(param.getGoodsData(), ShopManjianGoodsData.class);
        if (ActiveStatusEnum.ACTIVE.equals(ActiveStatusEnum.getEnumByStatus(param.getActiveStatus()))) {
            checkGoods(goodsDataList);
        }

        ShopNewcomerActiveValue activeValue = new ShopNewcomerActiveValue();
        activeValue.setValidityType(param.getValidityType());
        activeValue.setValidityDay(param.getValidityDay());
        activeValue.setValidityTime(ObjectUtil.isEmpty(param.getValidityTime()) ? 0 : DateUtils.StringToTimestamp(param.getValidityTime()));
        activeValue.setParticipationWay(param.getParticipationWay());
        activeValue.setAppointTime(ObjectUtil.isEmpty(param.getAppointTime()) ? 0 : DateUtils.StringToTimestamp(param.getAppointTime()));
        activeValue.setLimitNum(param.getLimitNum());
        activeValue.setBannerList(param.getBannerList());

        List<ShopActiveGoods> activeGoods = new ArrayList<>(CollectionUtil.size(goodsDataList));
        for (ShopManjianGoodsData goodsData : goodsDataList) {
            ShopActiveGoods goods = new ShopActiveGoods();
            goods.setSiteId(RequestUtils.siteId());
            goods.setGoodsId(goodsData.getGoodsId());
            goods.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
            goods.setActiveClass(ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
            goods.setActiveGoodsValue(JSON.toJSONString(goodsData));
            goods.setSkuId(goodsData.getSkuId());
            goods.setActiveGoodsStatus(param.getActiveStatus());
            goods.setActiveGoodsPrice(NumberUtil.toBigDecimal(goodsData.getNewcomerPrice()));
            activeGoods.add(goods);
        }

        LambdaQueryWrapper<ShopActive> activeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activeLambdaQueryWrapper.eq(ShopActive::getSiteId, RequestUtils.siteId())
                .eq(ShopActive::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
        ShopActive active = shopActiveMapper.selectOne(activeLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(active)) {
            ShopActive shopActive = new ShopActive();
            shopActive.setActiveDesc(param.getActiveDesc());
            shopActive.setSiteId(RequestUtils.siteId());
            shopActive.setActiveGoodsInfo(param.getGoodsData());
            shopActive.setActiveType(ActiveTypeEnum.GOODS.getType());
            shopActive.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
            shopActive.setActiveClass(ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
            shopActive.setActiveValue(JSON.toJSONString(activeValue));
            shopActive.setActiveStatus(param.getActiveStatus());
            coreActiveService.add(shopActive, activeGoods);
            return;
        }

        active.setSiteId(RequestUtils.siteId());
        active.setActiveStatus(param.getActiveStatus());
        active.setActiveDesc(param.getActiveDesc());
        active.setActiveGoodsInfo(param.getGoodsData());
        active.setActiveType(ActiveTypeEnum.GOODS.getType());
        active.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
        active.setActiveClass(ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
        active.setActiveValue(JSON.toJSONString(activeValue));
        activeGoods.forEach(goods -> goods.setActiveId(active.getActiveId()));
        coreActiveService.edit(active, activeGoods);
    }

    @Override
    public ShopNewcomerConfigVo getConfig() {
        CoreShopNewcomerConfigVo config = coreShopNewcomerService.getConfig(RequestUtils.siteId());
        ShopNewcomerConfigVo vo = new ShopNewcomerConfigVo();
        BeanUtils.copyProperties(config, vo);
        if (ObjectUtil.isEmpty(config)) {
            return vo;
        }

        LambdaQueryWrapper<ShopActiveGoods> shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopActiveGoodsLambdaQueryWrapper.eq(ShopActiveGoods::getSiteId, RequestUtils.siteId())
                .eq(ShopActiveGoods::getActiveId, config.getActiveId())
                .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
        List<ShopActiveGoods> shopActiveGoods = shopActiveGoodsMapper.selectList(shopActiveGoodsLambdaQueryWrapper);

        Set<Integer> goodsIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getGoodsId);
        Map<Integer, ShopGoods> goodsMap = Collections.emptyMap();
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            goodsMap = shopGoodsMapper.selectBatchIds(goodsIds).stream().collect(Collectors.toMap(ShopGoods::getGoodsId, e -> e));
        }

        Set<Integer> skuIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getSkuId);
        Map<Integer, ShopGoodsSku> skuMap = Collections.emptyMap();
        if (ObjectUtil.isNotEmpty(skuIds)) {
            skuMap = shopGoodsSkuMapper.selectBatchIds(skuIds).stream().collect(Collectors.toMap(ShopGoodsSku::getSkuId, e -> e));
        }

        List<ShopNewcomerConfigVo.ActiveGoods> activeGoodsList = new ArrayList<>(shopActiveGoods.size());
        for (ShopActiveGoods shopActiveGood : shopActiveGoods) {
            ShopGoods goods = goodsMap.getOrDefault(shopActiveGood.getGoodsId(), new ShopGoods());
            ShopGoodsSku sku = skuMap.getOrDefault(shopActiveGood.getSkuId(), new ShopGoodsSku());

            ShopNewcomerConfigVo.ActiveGoods activeGoods = new ShopNewcomerConfigVo.ActiveGoods();
            activeGoods.setGoodsId(shopActiveGood.getGoodsId());
            activeGoods.setSkuId(shopActiveGood.getSkuId());
            activeGoods.setGoodsName(goods.getGoodsName());
            activeGoods.setGoodsType(goods.getGoodsType());
            activeGoods.setGoodsTypeName(GoodsTypeEnum.getNameByType(goods.getGoodsType()));
            activeGoods.setSkuName(sku.getSkuName());
            activeGoods.setSkuImage(sku.getSkuImage());
            activeGoods.setPrice(sku.getPrice());
            activeGoods.setStock(sku.getStock());
            if (shopActiveGood.getActiveGoodsValue().startsWith("[")) {
                activeGoods.setActiveGoodsValue(JSONUtil.parseArray(shopActiveGood.getActiveGoodsValue()));
            } else {
                activeGoods.setActiveGoodsValue(JSONUtil.parseObj(shopActiveGood.getActiveGoodsValue()));
            }

            ShopNewcomerConfigVo.Goods goodsData = new ShopNewcomerConfigVo.Goods();
            BeanUtils.copyProperties(goods, goodsData);
            goodsData.setGoodsTypeName(GoodsTypeEnum.getNameByType(goods.getGoodsType()));
            activeGoods.setGoods(goodsData);

            ShopNewcomerConfigVo.GoodsSkuOne goodsSkuOne = new ShopNewcomerConfigVo.GoodsSkuOne();
            BeanUtils.copyProperties(sku, goodsSkuOne);
            activeGoods.setGoodsSkuOne(goodsSkuOne);

            activeGoodsList.add(activeGoods);
        }

        vo.setActiveGoods(activeGoodsList);
        return vo;
    }

    @Override
    public ShopNewComerSelectPageVo<ShopNewcomerShopGoodsVo> getSelectPage(PageParam pageParam, ShopNewcomerSelectPageParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        LambdaQueryWrapper<ShopActiveGoods> shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopActiveGoodsLambdaQueryWrapper.eq(ShopActiveGoods::getSiteId, RequestUtils.siteId())
                .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType())
                .eq(ShopActiveGoods::getActiveGoodsStatus, ActiveStatusEnum.ACTIVE.getStatus());
        List<ShopActiveGoods> shopActiveGoods = shopActiveGoodsMapper.selectList(shopActiveGoodsLambdaQueryWrapper);

        Set<Integer> activeGoodsIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getGoodsId);
        Set<Integer> activeSkuIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getSkuId);

        LambdaQueryWrapper<ShopGoods> shopGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopGoodsLambdaQueryWrapper.eq(ShopGoods::getSiteId, RequestUtils.siteId())
                .orderByDesc(ShopGoods::getSort)
                .orderByDesc(ShopGoods::getCreateTime);

        if (ObjectUtil.isNotEmpty(param.getGoodsCategory())) {
            shopGoodsLambdaQueryWrapper.eq(ShopGoods::getGoodsCategory, param.getGoodsCategory());
        }
        if (ObjectUtil.isNotEmpty(param.getGoodsType())) {
            shopGoodsLambdaQueryWrapper.eq(ShopGoods::getGoodsType, param.getGoodsType());
        }
        if (ObjectUtil.isNotEmpty(param.getKeyword())) {
            shopGoodsLambdaQueryWrapper.and(wrapper ->
                    wrapper.like(ShopGoods::getGoodsName, param.getKeyword()).or().like(ShopGoods::getSubTitle, param.getKeyword()));
        }

        switch (param.getSelectType()) {
            case "all":
                shopGoodsLambdaQueryWrapper.gt(ShopGoods::getStock, 0)
                        .eq(ShopGoods::getStatus, 1);
                if (ObjectUtil.isNotEmpty(activeGoodsIds)) {
                    shopGoodsLambdaQueryWrapper.in(ShopGoods::getGoodsId, activeGoodsIds);
                }
                break;
            case "selected":
                shopGoodsLambdaQueryWrapper.in(ShopGoods::getGoodsId, param.getGoodsIds());
                break;
        }

        Page<ShopGoods> iPage = shopGoodsMapper.selectPage(new Page<>(page, limit), shopGoodsLambdaQueryWrapper);
        Set<Integer> goodsIds = CollStreamUtil.toSet(iPage.getRecords(), ShopGoods::getGoodsId);

        LambdaQueryWrapper<ShopGoodsSku> shopGoodsSkuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopGoodsSkuLambdaQueryWrapper.eq(ShopGoodsSku::getIsDefault, 1);
        if (ObjectUtil.isNotEmpty(activeGoodsIds)) {
            shopGoodsSkuLambdaQueryWrapper.in(ShopGoodsSku::getSkuId, activeSkuIds);
        }

        if (ObjectUtil.isNotEmpty(goodsIds)) {
            shopGoodsSkuLambdaQueryWrapper.in(ShopGoodsSku::getGoodsId, goodsIds);
        }

        Map<Integer, List<ShopGoodsSku>> skuMap = shopGoodsSkuMapper.selectList(shopGoodsSkuLambdaQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId));

        Map<Integer, ShopActiveGoods> activeGoodsMap = shopActiveGoods
                .stream()
                .collect(Collectors.toMap(ShopActiveGoods::getSkuId, e -> e));

        List<ShopNewcomerShopGoodsVo> voList = new ArrayList<>(iPage.getRecords().size());
        for (ShopGoods record : iPage.getRecords()) {
            ShopNewcomerShopGoodsVo vo = new ShopNewcomerShopGoodsVo();
            BeanUtils.copyProperties(record, vo);
            List<ShopGoodsSku> skus = skuMap.getOrDefault(record.getGoodsId(), new ArrayList<>());
            if (ObjectUtil.isEmpty(skus)) {
                continue;
            }

            List<ShopNewcomerShopGoodsVo.GoodsSku> skuList = new ArrayList<>(skus.size());
            for (ShopGoodsSku sku : skus) {
                ShopActiveGoods activeGoods = activeGoodsMap.get(sku.getSkuId());
                if (activeGoods == null) {
                    continue;
                }

                ShopNewcomerShopGoodsVo.GoodsSku skuVo = new ShopNewcomerShopGoodsVo.GoodsSku();
                BeanUtils.copyProperties(sku, skuVo);
                JSONObject activeGoodsValue = JSON.parseObject(activeGoods.getActiveGoodsValue());
                BigDecimal newcomerPrice = Optional.ofNullable(activeGoodsValue.getBigDecimal("newcomer_price")).orElse(BigDecimal.ZERO);
                skuVo.setNewcomerPrice(newcomerPrice);
                skuList.add(skuVo);

                if (sku.getIsDefault() == 1) {
                    if (!activeSkuIds.contains(sku.getSkuId())) {
                        skuVo.setNewcomerPrice(skuList.get(0).getNewcomerPrice());
                    }

                    vo.setGoodsSku(skuVo);
                }
            }

            vo.setSkuList(skuList);
            voList.add(vo);
        }

        List<Integer> verifyGoodsId = Collections.emptyList();
        if (ObjectUtil.isAllNotEmpty(param.getVerifyGoodsIds(), activeGoodsIds)) {
            LambdaQueryWrapper<ShopGoods> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ShopGoods::getGoodsId, param.getVerifyGoodsIds())
                    .eq(ShopGoods::getStatus, 1);
            verifyGoodsId = shopGoodsMapper.selectList(queryWrapper)
                    .stream()
                    .map(ShopGoods::getGoodsId)
                    .filter(activeGoodsIds::contains)
                    .collect(Collectors.toList());
        }

        List<Integer> verifySkuIds = Collections.emptyList();
        if (ObjectUtil.isAllNotEmpty(param.getVerifySkuIds(), activeSkuIds)) {
            LambdaQueryWrapper<ShopGoodsSku> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ShopGoodsSku::getSkuId, param.getVerifySkuIds());
            verifySkuIds = shopGoodsSkuMapper.selectList(queryWrapper)
                    .stream()
                    .map(ShopGoodsSku::getSkuId)
                    .filter(activeSkuIds::contains)
                    .collect(Collectors.toList());
        }

        ShopNewComerSelectPageVo<ShopNewcomerShopGoodsVo> vo = new ShopNewComerSelectPageVo<>(iPage, voList);
        return vo.setVerifyGoodsIds(verifyGoodsId)
                .setVerifySkuIds(verifySkuIds);
    }

    @Override
    public List<ShopNewcomerSelectSkuVo> getSelectSku(ShopNewcomerSelectSkuParam param) {
        LambdaQueryWrapper<ShopActiveGoods> shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopActiveGoodsLambdaQueryWrapper.eq(ShopActiveGoods::getSiteId, RequestUtils.siteId())
                .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType())
                .eq(ShopActiveGoods::getActiveGoodsStatus, ActiveStatusEnum.ACTIVE.getStatus());
        List<ShopActiveGoods> shopActiveGoods = shopActiveGoodsMapper.selectList(shopActiveGoodsLambdaQueryWrapper);

        Set<Integer> activeGoodsIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getGoodsId);
        Set<Integer> activeSkuIds = CollStreamUtil.toSet(shopActiveGoods, ShopActiveGoods::getSkuId);

        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        List<ShopNewcomerSelectSkuVo> list = new LinkedList<>();
        if (ObjectUtil.isAllNotEmpty(param.getVerifyGoodsIds(), activeGoodsIds)) {
            LambdaQueryWrapper<ShopGoods> shopGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            shopGoodsLambdaQueryWrapper.in(ShopGoods::getGoodsId, param.getVerifyGoodsIds())
                    .eq(ShopGoods::getStatus, 1);
            List<Integer> verifyGoodsIds = shopGoodsMapper.selectList(shopGoodsLambdaQueryWrapper)
                    .stream()
                    .map(ShopGoods::getGoodsId)
                    .filter(activeGoodsIds::contains)
                    .collect(Collectors.toList());

            shopGoodsLambdaQueryWrapper.clear();
            shopGoodsLambdaQueryWrapper.in(ShopGoods::getGoodsId, verifyGoodsIds)
                    .orderByDesc(ShopGoods::getSort)
                    .orderByDesc(ShopGoods::getCreateTime);
            List<ShopGoods> shopGoods = shopGoodsMapper.selectList(shopGoodsLambdaQueryWrapper);
            Set<Integer> goodsIds = CollStreamUtil.toSet(shopGoods, ShopGoods::getGoodsId);

            LambdaQueryWrapper<ShopGoodsSku> shopGoodsSkuLambdaQueryWrapper = new LambdaQueryWrapper<>();
            shopGoodsSkuLambdaQueryWrapper.in(ShopGoodsSku::getGoodsId, goodsIds);

            Map<Integer, List<ShopGoodsSku>> skuMap = shopGoodsSkuMapper.selectList(shopGoodsSkuLambdaQueryWrapper)
                    .stream()
                    .collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId));

            for (ShopGoods shopGood : shopGoods) {
                ShopNewcomerSelectSkuVo vo = new ShopNewcomerSelectSkuVo();
                BeanUtils.copyProperties(shopGood, vo);
                vo.setGoodsTypeName(GoodsTypeEnum.getNameByType(shopGood.getGoodsType()));
                vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), shopGood.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
                vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), shopGood.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
                List<ShopGoodsSku> shopGoodsSkuList = skuMap.getOrDefault(shopGood.getGoodsId(), new ArrayList<>());

                List<ShopNewcomerSelectSkuVo.Sku> skuList = new ArrayList<>(shopGoodsSkuList.size());
                for (ShopGoodsSku sku : shopGoodsSkuList) {
                    ShopNewcomerSelectSkuVo.Sku skuVo = new ShopNewcomerSelectSkuVo.Sku();
                    BeanUtils.copyProperties(sku, skuVo);
                    JSONObject activeGoodsValue = JSON.parseObject(shopActiveGoods.get(sku.getSkuId()).getActiveGoodsValue());
                    skuVo.setNewcomerPrice(Optional.ofNullable(activeGoodsValue.getBigDecimal("newcomer_price")).orElse(BigDecimal.ZERO));

                    if (sku.getIsDefault() == 1) {
                        if (!activeSkuIds.contains(sku.getSkuId())) {
                            skuVo.setNewcomerPrice(skuList.get(0).getNewcomerPrice());
                        }

                        vo.setGoodsSku(skuVo);
                    }

                    skuList.add(skuVo);
                }
                list.add(vo);
            }
        }

        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        if (ObjectUtil.isAllNotEmpty(param.getVerifySkuIds(), activeSkuIds)) {
            LambdaQueryWrapper<ShopGoodsSku> shopGoodsSkuLambdaQueryWrapper = new LambdaQueryWrapper<>();
            shopGoodsSkuLambdaQueryWrapper.in(ShopGoodsSku::getSkuId, param.getVerifySkuIds());
            List<ShopGoodsSku> skus = shopGoodsSkuMapper.selectList(shopGoodsSkuLambdaQueryWrapper)
                    .stream()
                    .filter(sku -> activeSkuIds.contains(sku.getSkuId()))
                    .collect(Collectors.toList());

            Set<Integer> skuIds = CollStreamUtil.toSet(skus, ShopGoodsSku::getSkuId);

            shopGoodsSkuLambdaQueryWrapper.clear();
            shopGoodsSkuLambdaQueryWrapper.in(ShopGoodsSku::getSkuId, skuIds);
            Set<Integer> goodsIds = shopGoodsSkuMapper.selectList(shopGoodsSkuLambdaQueryWrapper)
                    .stream()
                    .map(ShopGoodsSku::getGoodsId)
                    .collect(Collectors.toSet());

            LambdaQueryWrapper<ShopGoods> shopGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            shopGoodsLambdaQueryWrapper.in(ShopGoods::getGoodsId, goodsIds)
                    .orderByDesc(ShopGoods::getSort)
                    .orderByDesc(ShopGoods::getCreateTime);
            List<ShopGoods> shopGoods = shopGoodsMapper.selectList(shopGoodsLambdaQueryWrapper);

            shopGoodsSkuLambdaQueryWrapper.clear();
            shopGoodsSkuLambdaQueryWrapper.in(ShopGoodsSku::getSkuId, skuIds)
                    .in(ShopGoodsSku::getGoodsId, CollStreamUtil.toSet(shopGoods, ShopGoods::getGoodsId));
            Map<Integer, List<ShopGoodsSku>> goodsSkuMap = shopGoodsSkuMapper.selectList(shopGoodsSkuLambdaQueryWrapper)
                    .stream()
                    .collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId));

            Map<Integer, ShopActiveGoods> shopActiveGoodsMap = shopActiveGoods.stream().collect(Collectors.toMap(ShopActiveGoods::getSkuId, e -> e));

            for (ShopGoods shopGood : shopGoods) {
                ShopNewcomerSelectSkuVo vo = new ShopNewcomerSelectSkuVo();
                BeanUtils.copyProperties(shopGood, vo);
                List<ShopGoodsSku> skuList = goodsSkuMap.get(shopGood.getGoodsId());
                if (ObjectUtil.hasEmpty(skus, shopActiveGoods)) {
                    continue;
                }

                List<ShopNewcomerSelectSkuVo.Sku> skuVoList = new ArrayList<>(skuList.size());
                for (ShopGoodsSku sku : skuList) {
                    ShopNewcomerSelectSkuVo.Sku skuVo = new ShopNewcomerSelectSkuVo.Sku();
                    BeanUtils.copyProperties(sku, skuVo);
                    ShopActiveGoods activeGoods = shopActiveGoodsMap.get(sku.getSkuId());
                    JSONObject activeGoodsValue = JSON.parseObject(activeGoods.getActiveGoodsValue());
                    skuVo.setNewcomerPrice(Optional.ofNullable(activeGoodsValue.getBigDecimal("newcomer_price")).orElse(BigDecimal.ZERO));

                    if (sku.getIsDefault() == 1) {
                        skuVo.setNewcomerPrice(skuVoList.get(0).getNewcomerPrice());
                    }

                    skuVoList.add(skuVo);
                }

                list.add(vo);
            }
        }

        return list;
    }

    private void checkGoods(List<ShopManjianGoodsData> goodsData) {
        if (ObjectUtil.isEmpty(goodsData)) {
            throw new RuntimeException("请选择参与活动商品");
        }
        for (ShopManjianGoodsData goods : goodsData) {
            if (ObjectUtil.isEmpty(goods)) {
                throw new RuntimeException("商品规格不能为空");
            }
            if (ObjectUtil.isEmpty(goods.getNewcomerPrice())) {
                throw new RuntimeException("新人价不能为空");
            }
        }
    }
}
