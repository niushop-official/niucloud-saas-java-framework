package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsRank;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.goods.GoodsRankTypeEnum;
import com.niu.shop.enums.goods.GoodsRuleTypeEnum;
import com.niu.shop.enums.goods.GoodsSourceTypeEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsRankMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.goods.IShopGoodsRankService;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.*;
import com.niu.shop.service.core.goods.ICoreGoodsRankService;
import com.niu.shop.service.core.goods.param.ShopGoodsRankConfigParam;
import com.niu.shop.service.core.goods.vo.ShopGoodsRankConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品排行榜实现
 */
@Service
@RequiredArgsConstructor
public class ShopGoodsRankServiceImpl implements IShopGoodsRankService {
    private final ShopGoodsMapper shopGoodsMapper;
    private final ShopGoodsSkuMapper shopGoodsSkuMapper;
    private final ShopGoodsRankMapper shopGoodsRankMapper;
    private final ICoreGoodsRankService coreGoodsRankService;

    @Override
    public PageResult<ShopGoodsRankListVo> page(PageParam pageParam, ShopGoodsRankSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, "desc".equalsIgnoreCase(searchParam.getSort()), searchParam.getOrder());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getName())) {
            queryWrapper.like("name", searchParam.getName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getRankType())) {
            queryWrapper.eq("rank_type", searchParam.getRankType());
        }

        IPage<ShopGoodsRank> iPage = shopGoodsRankMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsRankListVo> list = new ArrayList<>(CollectionUtil.size(iPage.getRecords()));
        for (ShopGoodsRank item : iPage.getRecords()) {
            ShopGoodsRankListVo vo = new ShopGoodsRankListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setRankTypeName(GoodsRankTypeEnum.getNameByValue(item.getRankType()));
            vo.setGoodsSourceName(GoodsSourceTypeEnum.getNameByValue(item.getGoodsSource()));
            vo.setRuleTypeName(GoodsRuleTypeEnum.getNameByValue(item.getRuleType()));
            vo.setShowGoodsNum(checkRankData(item));
            vo.setGoodsJson(ObjectUtil.isEmpty(item.getGoodsJson()) ? new JSONArray() : JSONUtil.parseArray(item.getGoodsJson()));
            vo.setBrandIds(ObjectUtil.isEmpty(item.getBrandIds()) || !item.getBrandIds().startsWith("[") ? new JSONArray() : JSONUtil.parseArray(item.getBrandIds()));
            vo.setCategoryIds(ObjectUtil.isEmpty(item.getCategoryIds())|| !item.getCategoryIds().startsWith("[") ? new JSONArray() : JSONUtil.parseArray(item.getCategoryIds()));
            vo.setLabelIds(ObjectUtil.isEmpty(item.getLabelIds())|| !item.getLabelIds().startsWith("[") ? new JSONArray() : JSONUtil.parseArray(item.getLabelIds()));
            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));
            list.add(vo);
        }
        return PageResult.build(iPage, list);
    }

    @Override
    public void add(ShopGoodsRankParam addParam) {
        QueryWrapper<ShopGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(addParam.getName())) {
            queryWrapper.eq("name", addParam.getName());
        }
        if (shopGoodsRankMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("商品榜单已存在，请检查");
        }

        ShopGoodsRank model = checkRankData(addParam, new ShopGoodsRank());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setSiteId(RequestUtils.siteId());
        shopGoodsRankMapper.insert(model);
    }

    @Override
    public void edit(Integer id, ShopGoodsRankParam editParam) {
        ShopGoodsRank model = shopGoodsRankMapper.selectOne(
                new QueryWrapper<ShopGoodsRank>()
                        .eq("rank_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品排行榜不存在");

        QueryWrapper<ShopGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("name", editParam.getName())
                .ne("rank_id", editParam.getRankId());
        if (shopGoodsRankMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("商品榜单已存在");
        }

        checkRankData(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopGoodsRankMapper.updateById(model);
    }

    @Override
    public ShopGoodsRankInfoVo info(Integer id) {
        ShopGoodsRank model = shopGoodsRankMapper.selectOne(
                new QueryWrapper<ShopGoodsRank>()
                        .eq("rank_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品排行榜不存在");

        ShopGoodsRankInfoVo vo = new ShopGoodsRankInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setCreateTime(DateUtils.timestampToString(model.getCreateTime()));
        vo.setUpdateTime(DateUtils.timestampToString(model.getUpdateTime()));
        vo.setGoodsJson(ObjectUtil.isEmpty(model.getGoodsJson()) ? Collections.emptyList() : JSONUtil.toList(JSONUtil.parseArray(model.getGoodsJson()), ShopGoodsRankInfoVo.GoodsJson.class));
        vo.setGoodsList(ObjectUtil.isEmpty(model.getGoodsJson()) ? Collections.emptyList() : JSONUtil.parseArray(model.getGoodsJson()));
        vo.setCategoryIds(ObjectUtil.isEmpty(model.getCategoryIds())  || !model.getCategoryIds().startsWith("[") ? Collections.emptyList() : JSONUtil.parseArray(model.getCategoryIds()));
        vo.setLabelIds(ObjectUtil.isEmpty(model.getLabelIds())   || !model.getLabelIds().startsWith("[") ? Collections.emptyList() : JSONUtil.parseArray(model.getLabelIds()));
        vo.setBrandIds(ObjectUtil.isEmpty(model.getBrandIds()) || !model.getBrandIds().startsWith("[") ? Collections.emptyList() : JSONUtil.parseArray(model.getBrandIds()));

        if (ObjectUtil.isEmpty(model.getGoodsSource()) || !GoodsSourceTypeEnum.GOODS.getValue().equals(model.getGoodsSource())) {
            return vo;
        }

        List<Integer> goodsId = JSONUtil.parseArray(model.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .map(g -> g.getInt("goodsId"))
                .toList();

        Map<Integer, Integer> goodsSortMap = JSONUtil.parseArray(model.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .collect(Collectors.toMap(g -> g.getInt("goodsId"), g -> g.getInt("sort", 0)));

        List<ShopGoods> shopGoods = Collections.emptyList();
        Map<Integer, List<ShopGoodsSku>> skuMap = Collections.emptyMap();
        if (ObjectUtil.isNotEmpty(goodsId)) {
            QueryWrapper<ShopGoods> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.eq("site_id", RequestUtils.siteId())
                    .in("goods_id", goodsId);
            shopGoods = shopGoodsMapper.selectList(goodsQueryWrapper);

            QueryWrapper<ShopGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
            shopGoodsSkuQueryWrapper.in("goods_id", goodsId);
            skuMap = shopGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper).stream().collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId));
        }

        List<ShopGoodsRankInfoVo.GoodsList> goodsList = new ArrayList<>(CollectionUtil.size(shopGoods));
        for (ShopGoods shopGood : shopGoods) {
            shopGood.setSort(0);
            if (goodsSortMap.containsKey(shopGood.getGoodsId())) {
                shopGood.setSort(goodsSortMap.get(shopGood.getGoodsId()));
            }
            ShopGoodsRankInfoVo.GoodsList info = new ShopGoodsRankInfoVo.GoodsList();
            BeanUtils.copyProperties(shopGood, info);
            info.setGoodsTypeName(GoodsTypeEnum.getNameByType(shopGood.getGoodsType()));

            ShopGoodsRankInfoVo.GoodsList.GoodsSkuDTO sku = new ShopGoodsRankInfoVo.GoodsList.GoodsSkuDTO();
            List<ShopGoodsSku> skuList = skuMap.get(info.getGoodsId());
            ShopGoodsSku goodsSku = ObjectUtil.isEmpty(skuList) ? null : skuList.get(0);
            if (goodsSku != null) {
                BeanUtils.copyProperties(goodsSku, sku);
                sku.setPrice(goodsSku.getPrice().toString());
            }

            info.setGoodsSku(sku);
            goodsList.add(info);
        }

        vo.setGoodsList(goodsList);
        return vo;
    }

    @Override
    public ShopGoodsRankOptionData getOptionData() {
        JSONObject type = new JSONObject();
        for (GoodsRankTypeEnum value : GoodsRankTypeEnum.values()) {
            type.set(value.getValue(), value.getName());
        }
        JSONObject source = new JSONObject();
        for (GoodsSourceTypeEnum value : GoodsSourceTypeEnum.values()) {
            source.set(value.getValue(), value.getName());
        }
        JSONObject rule = new JSONObject();
        for (GoodsRuleTypeEnum value : GoodsRuleTypeEnum.values()) {
            rule.set(value.getValue(), value.getName());
        }
        return new ShopGoodsRankOptionData(type, source, rule);
    }

    @Override
    public void del(Integer id) {
        QueryWrapper<ShopGoodsRank> queryWrapper = new QueryWrapper<ShopGoodsRank>().eq("rank_id", id).eq("site_id", RequestUtils.siteId());
        ShopGoodsRank rank = shopGoodsRankMapper.selectOne(queryWrapper);
        Assert.notNull(rank, "商品榜单不存在，请检查");
        shopGoodsRankMapper.deleteById(rank);
    }

    @Override
    public ShopGoodsRankConfigVo getGoodsRankConfig() {
        return coreGoodsRankService.getGoodsRankConfig(RequestUtils.siteId());
    }

    @Override
    public void setGoodsRankConfig(ShopGoodsRankConfigParam param) {
        param.setSiteId(RequestUtils.siteId());
        coreGoodsRankService.setGoodsRankConfig(param);
    }

    @Override
    public void editSort(ShopGoodsRankEditSortParam param) {
        if (ObjectUtil.isEmpty(param.getRankId())) {
            throw new RuntimeException("商品榜单不存在");
        }

        ShopGoodsRank rank = shopGoodsRankMapper.selectById(param.getRankId());
        if (ObjectUtil.isEmpty(rank)) {
            throw new RuntimeException("商品榜单不存在");
        }

        rank.setSort(param.getSort());
        shopGoodsRankMapper.updateById(rank);
    }

    @Override
    public void batchDelete(ShopGoodsRankBatchDeleteParam param) {
        if (ObjectUtil.isEmpty(param.getRankId())) {
            throw new RuntimeException("商品榜单不存在");
        }
        shopGoodsRankMapper.deleteBatchIds(param.getRankId());
    }

    @Override
    public ShopGoodsRankSelectPageResult getSelectPage(PageParam pageParam, ShopGoodsRankSelectPageParam param) {
        List<Integer> verifyRankIds = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(param.getVerifyRankIds())) {
            verifyRankIds = shopGoodsRankMapper.selectBatchIds(param.getVerifyRankIds())
                    .stream()
                    .map(ShopGoodsRank::getRankId)
                    .toList();
        }

        QueryWrapper<ShopGoodsRank> rankQueryWrapper = new QueryWrapper<>();
        rankQueryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("sort")
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(param.getName())) {
            rankQueryWrapper.like("name", param.getName());
        }

        if (ObjectUtil.isNotEmpty(param.getRankType())) {
            rankQueryWrapper.eq("rank_type", param.getRankType());
        }

        if (ObjectUtil.isNotEmpty(param.getStatus())) {
            rankQueryWrapper.eq("status", param.getStatus());
        }

        Page<ShopGoodsRank> shopGoodsRankPage = shopGoodsRankMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), rankQueryWrapper);
        List<ShopGoodsRankSelectPageInfo> list = new ArrayList<>(CollectionUtil.size(shopGoodsRankPage.getRecords()));
        for (ShopGoodsRank record : shopGoodsRankPage.getRecords()) {
            ShopGoodsRankSelectPageInfo info = new ShopGoodsRankSelectPageInfo();
            BeanUtils.copyProperties(record, info);
            info.setShowGoodsNum(checkRankData(record));
            info.setRankTypeName(GoodsRankTypeEnum.getNameByValue(record.getRankType()));
            info.setGoodsSourceName(GoodsSourceTypeEnum.getNameByValue(record.getGoodsSource()));
            info.setRuleTypeName(GoodsRuleTypeEnum.getNameByValue(record.getRuleType()));
            list.add(info);
        }

        return new ShopGoodsRankSelectPageResult(shopGoodsRankPage, list, verifyRankIds);
    }

    @Override
    public void modifyStatus(ShopGoodsRankModifyStatusParam param) {
        ShopGoodsRank rank = shopGoodsRankMapper.selectById(param.getRankId());
        rank.setStatus(param.getStatus());
        shopGoodsRankMapper.updateById(rank);
    }

    private long checkRankData(ShopGoodsRank item) {
        QueryWrapper<ShopGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("delete_time", 0)
                .eq("status", 1);

        switch (GoodsSourceTypeEnum.getEnumByValue(item.getGoodsSource())) {
            case GOODS -> {
                JSONArray goodsJson = JSONUtil.parseArray(item.getGoodsJson());
                List<Integer> goodsId = goodsJson.stream()
                        .map(JSONUtil::parseObj)
                        .map(goods -> goods.getInt("goodsId"))
                        .toList();
                if (ObjectUtil.isNotEmpty(goodsId)) {
                    goodsQueryWrapper.in("goods_id", goodsId);
                }
            }
            case CATEGORY -> {
                JSONArray categoryIds = JSONUtil.parseArray(item.getCategoryIds());
                if (ObjectUtil.isNotEmpty(categoryIds)) {
                    goodsQueryWrapper.and(wrapper -> categoryIds.forEach(id -> wrapper.like("category_ids", id)));
                }
            }
            case BRAND -> {
                JSONArray brandIds = JSONUtil.parseArray(item.getBrandIds());
                if (ObjectUtil.isNotEmpty(brandIds)) {
                    goodsQueryWrapper.in("brand_ids", brandIds);
                }
            }
            case LABEL -> {
                JSONArray labels = JSONUtil.parseArray(item.getLabelIds());
                if (ObjectUtil.isNotEmpty(labels)) {
                    goodsQueryWrapper.and(wrapper -> labels.forEach(id -> wrapper.like("label_ids", id)));
                }
            }
        }

        return shopGoodsMapper.selectCount(goodsQueryWrapper);
    }

    private ShopGoodsRank checkRankData(ShopGoodsRankParam param, ShopGoodsRank model) {
        BeanUtils.copyProperties(param, model);
        switch (GoodsSourceTypeEnum.getEnumByValue(param.getGoodsSource())) {
            case GOODS -> {
                model.setGoodsJson(ObjectUtil.isEmpty(param.getGoodsJson()) ? "" : JSONUtil.toJsonStr(param.getGoodsJson()));
                model.setCategoryIds("");
                model.setBrandIds("");
                model.setLabelIds("");
            }
            case CATEGORY -> {
                model.setCategoryIds(ObjectUtil.isEmpty(param.getCategoryIds()) ? "" : JSONUtil.toJsonStr(param.getCategoryIds()));
                model.setGoodsJson("");
                model.setBrandIds("");
                model.setLabelIds("");
            }
            case BRAND -> {
                model.setBrandIds(ObjectUtil.isEmpty(param.getBrandIds()) ? "" : JSONUtil.toJsonStr(param.getBrandIds()));
                model.setCategoryIds("");
                model.setGoodsJson("");
                model.setLabelIds("");
            }
            case LABEL -> {
                model.setLabelIds(ObjectUtil.isEmpty(param.getLabelIds()) ? "" : JSONUtil.toJsonStr(param.getLabelIds()));
                model.setCategoryIds("");
                model.setBrandIds("");
                model.setGoodsJson("");
            }
        }
        return model;
    }

}
