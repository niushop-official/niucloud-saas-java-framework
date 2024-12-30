package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsRank;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.goods.GoodsRankTypeEnum;
import com.niu.shop.enums.goods.GoodsSourceTypeEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsRankMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.goods.param.ShopGoodsRankSearchParam;
import com.niu.shop.service.api.goods.IGoodsRankService;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.param.GoodsRankComponentParam;
import com.niu.shop.service.api.goods.param.ShopGoodsRankGoodsPageParam;
import com.niu.shop.service.api.goods.vo.GoodsRankComponentVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsRankGoodsListInfo;
import com.niu.shop.service.api.goods.vo.ShopGoodsRankGoodsPageVo;
import com.niu.shop.service.core.goods.ICoreGoodsRankService;
import com.niu.shop.service.core.goods.vo.ShopGoodsRankConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsRankServiceImpl implements IGoodsRankService {
    private final IGoodsService goodsService;
    private final ShopGoodsMapper shopGoodsMapper;
    private final ICoreUploadService coreUploadService;
    private final ShopGoodsSkuMapper shopGoodsSkuMapper;
    private final IMemberLevelService memberLevelService;
    private final ShopGoodsRankMapper shopGoodsRankMapper;
    private final ICoreGoodsRankService coreGoodsRankService;

    @Override
    public ShopGoodsRankConfigVo getGoodsRankConfig() {
        return coreGoodsRankService.getGoodsRankConfig(RequestUtils.siteId());
    }

    @Override
    public List<ShopGoodsRank> page(ShopGoodsRankSearchParam searchParam) {
        QueryWrapper<ShopGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("status", 1)
                .orderByDesc("sort")
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getName())) {
            queryWrapper.like("name", searchParam.getName());
        }

        return shopGoodsRankMapper.selectList(queryWrapper);
    }

    @Override
    public ShopGoodsRankGoodsPageVo getGoodsPage(PageParam pageParam, ShopGoodsRankGoodsPageParam param) {
        QueryWrapper<ShopGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("rank_id", param.getRankId());
        List<ShopGoodsRank> ranks = shopGoodsRankMapper.selectList(queryWrapper);

        if (ObjectUtil.isEmpty(ranks)) {
            return new ShopGoodsRankGoodsPageVo();
        }

        ShopGoodsRank rank = ranks.get(0);
        return new ShopGoodsRankGoodsPageVo(getGoodsList(pageParam, ranks, 0), rank.getRankId(), rank.getName());
    }

    @Override
    public GoodsRankComponentVo getRankComponents(PageParam pageParam, GoodsRankComponentParam param) {
        QueryWrapper<ShopGoodsRank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("status", 1);
        if (param.getRankId() > 0) {
            queryWrapper.eq("rank_id", param.getRankId())
                    .orderByDesc("sort")
                    .orderByDesc("create_time");
        }

        List<ShopGoodsRank> ranks = shopGoodsRankMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(ranks)) {
            return new GoodsRankComponentVo();
        }

        GoodsRankComponentVo vo = new GoodsRankComponentVo();
        vo.setRankId(ranks.get(0).getRankId());
        vo.setName(ranks.get(0).getName());
        vo.setGoodsList(getGoodsList(pageParam, ranks, 3).getData());
        return vo;
    }

    private PageResult<ShopGoodsRankGoodsListInfo> getGoodsList(PageParam pageParam, List<ShopGoodsRank> ranks, int limit) {
        if (ObjectUtil.isEmpty(ranks)) {
            return new PageResult<>();
        }

        ShopGoodsRank rank = ranks.get(0);
        long dateEnd = System.currentTimeMillis() / 1000;
        long dateStart = switch (GoodsRankTypeEnum.getEnumByValue(rank.getRankType())) {
            case DAY -> (DateUtil.beginOfDay(new Date()).getTime()) / 1000;
            case WEEK -> (DateUtil.beginOfWeek(new Date()).getTime()) / 1000;
            case MONTH -> (DateUtil.beginOfMonth(new Date()).getTime()) / 1000;
            case QUARTER -> (DateUtil.beginOfQuarter(new Date()).getTime()) / 1000;
        };

        GoodsSourceTypeEnum source = GoodsSourceTypeEnum.getEnumByValue(rank.getGoodsSource());
        if (GoodsSourceTypeEnum.GOODS.equals(source)) {
            List<ShopGoodsRankGoodsListInfo> list = getGoodsListGoodsSource(rank, dateStart, dateEnd);

            if (limit == 0) {
                PageResult<ShopGoodsRankGoodsListInfo> result = PageResult.build(1, 1, list.size(), list);
                return getGoodsListPage(result);
            }

            if (CollectionUtil.size(list) > limit) {
                list = list.subList(0, limit);
            }

            List<ShopGoodsRankGoodsListInfo> infos = getGoodsListAll(list);
            return PageResult.build(1, 1, infos.size(), infos);
        }

        String order = "stat_" + rank.getRuleType() + "_num";
        MPJQueryWrapper<ShopGoods> goodsMPJQueryWrapper = new MPJQueryWrapper<>();
        goodsMPJQueryWrapper.setAlias("goods")
                .eq("goods.site_id", RequestUtils.siteId())
                .eq("goods.status", 1)
                .eq("goods.delete_time", 0)
                .orderByDesc(order)
                .orderByDesc("goods_sort")
                .orderByDesc("create_time")
                .groupBy("goods.goods_id, goods.sort, goods.create_time")
                .leftJoin("nc_shop_goods_stat stat on goods.goods_id = stat.goods_id and date_time BETWEEN " + dateStart + " AND " + dateEnd)
                .select("goods.site_id,goods.goods_id,goods_name,goods_cover,unit,goods.sale_num + goods.virtual_sale_num as sale_num,member_discount,sum(stat.sale_num)+ goods.virtual_sale_num as stat_sale_num,sum(stat.collect_num) as stat_collect_num,sum(stat.evaluate_num) as stat_evaluate_num,sum(stat.access_num) as stat_access_num,goods.create_time,goods.sort as goods_sort");

        if (limit > 0) {
            goodsMPJQueryWrapper.last("limit " + limit);
        }

        MPJQueryWrapper<ShopGoods> queryWrapper = switch (source) {
            case CATEGORY ->
                    goodsMPJQueryWrapper.and(wrapper -> JSONUtil.parseArray(rank.getCategoryIds()).forEach(id -> wrapper.or().like("goods_category", id)));
            case BRAND -> goodsMPJQueryWrapper.in("goods.brand_id", JSONUtil.parseArray(rank.getBrandIds()));
            case LABEL ->
                    goodsMPJQueryWrapper.and(wrapper -> JSONUtil.parseArray(rank.getLabelIds()).forEach(id -> wrapper.or().like("label_ids", id)));
            default -> goodsMPJQueryWrapper;
        };

        if (limit == 0) {
            Page<Map<String, Object>> iPage = shopGoodsMapper.selectMapsPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper);
            IPage<ShopGoodsRankGoodsListInfo> convert = iPage.convert(map -> {
                ShopGoodsRankGoodsListInfo info = new ShopGoodsRankGoodsListInfo();
                ReflectionUtils.doWithFields(info.getClass(), field -> {
                    field.setAccessible(true);

                    Object v = map.get(StrUtil.toUnderlineCase(field.getName()));

                    if (v == null) {
                        return;
                    }

                    if (field.getName().equals("createTime")) {
                        field.set(info, DateUtils.timestampToString(Long.parseLong(v.toString())));
                        return;
                    }

                    if (field.getType().equals(Integer.class)) {
                        field.set(info, Integer.parseInt(v.toString()));
                        return;
                    }

                    field.set(info, v);
                });

                String thumb = coreUploadService.thumb(info.getSiteId(), info.getGoodsCover(),
                        UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                info.setGoodsCoverThumbMid(thumb);
                return info;
            });

            return getGoodsListPage(PageResult.build(convert));
        }

        List<ShopGoodsRankGoodsListInfo> list = shopGoodsMapper.selectJoinList(ShopGoodsRankGoodsListInfo.class, queryWrapper);
        List<ShopGoodsRankGoodsListInfo> infos = getGoodsListAll(list);
        return PageResult.build(1, 1, infos.size(), infos);
    }

    private List<ShopGoodsRankGoodsListInfo> getGoodsListAll(List<ShopGoodsRankGoodsListInfo> list) {
        if (ObjectUtil.isEmpty(list)) {
            return list;
        }

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
        for (int i = 0; i < list.size(); i++) {
            ShopGoodsRankGoodsListInfo info = list.get(i);
            info.setRankNum(i + 1);
            ShopGoodsSku sku = getGoodsSku(info.getGoodsId());
            info.setGoodsSku(sku);

            String thumb = coreUploadService.thumb(RequestUtils.siteId(), info.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            info.setGoodsCoverThumbMid(thumb);

            if (sku == null) {
                continue;
            }

            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
            BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, info.getMemberDiscount(), memberPriceConfig, sku.getPrice());
            info.getGoodsSku().setMemberPrice(memberPrice.toString());
        }

        return list;
    }

    private PageResult<ShopGoodsRankGoodsListInfo> getGoodsListPage(PageResult<ShopGoodsRankGoodsListInfo> pageResult) {
        if (ObjectUtil.isEmpty(pageResult.getData())) {
            return pageResult;
        }

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
        for (int i = 0; i < pageResult.getData().size(); i++) {
            ShopGoodsRankGoodsListInfo info = pageResult.getData().get(i);
            info.setRankNum(i + 1);
            ShopGoodsSku sku = getGoodsSku(info.getGoodsId());
            info.setGoodsSku(sku);

            if (sku == null) {
                continue;
            }

            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
            BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, info.getMemberDiscount(), memberPriceConfig, sku.getPrice());
            sku.setMemberPrice(memberPrice.toString());
        }

        return pageResult;
    }

    private List<ShopGoodsRankGoodsListInfo> getGoodsListGoodsSource(ShopGoodsRank rank, long dateStart, long dateEnd) {
        List<Integer> configGoodsIds = JSONUtil.parseArray(rank.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .map(o -> o.getInt("goodsId"))
                .toList();

        Map<Integer, JSONObject> sortGoodsJson = JSONUtil.parseArray(rank.getGoodsJson())
                .stream()
                .map(JSONUtil::parseObj)
                .collect(Collectors.toMap(o -> o.getInt("goodsId"), o -> o));

        MPJQueryWrapper<ShopGoods> goodsMPJQueryWrapper = new MPJQueryWrapper<>();
        goodsMPJQueryWrapper.setAlias("goods")
                .eq("goods.site_id", RequestUtils.siteId())
                .in("goods.goods_id", configGoodsIds)
                .eq("goods.is_gift", 0)
                .eq("goods.status", 1)
                .eq("goods.delete_time", 0)
                .orderByDesc("stat_" + rank.getRuleType() + "_num")
                .orderByDesc("goods_sort")
                .orderByDesc("create_time")
                .leftJoin("nc_shop_goods_stat stat on goods.goods_id = stat.goods_id  AND date_time BETWEEN " + dateStart + " AND " + dateEnd)
                .groupBy("goods.goods_id, goods_sort, create_time")
                .select("goods.site_id,goods.goods_id,goods_name,goods_cover,unit,goods.sale_num + goods.virtual_sale_num as sale_num,member_discount,sum(stat.sale_num)+ goods.virtual_sale_num as stat_sale_num,sum(stat.collect_num) as stat_collect_num,sum(stat.evaluate_num) as stat_evaluate_num,sum(stat.access_num) as stat_access_num, goods.create_time create_time,goods.sort as goods_sort");

        return shopGoodsMapper.selectJoinMaps(goodsMPJQueryWrapper)
                .stream().map(JSONUtil::parseObj)
                .map(o -> {
                    ShopGoodsRankGoodsListInfo info = o.toBean(ShopGoodsRankGoodsListInfo.class);
                    Integer sort = Optional.ofNullable(sortGoodsJson.get(info.getGoodsId()))
                            .map(obj -> obj.getInt("sort"))
                            .orElse(0);
                    info.setSort(sort);
                    info.setCreateTime(DateUtils.timestampToString(o.getLong("create_time")));

                    String cover = coreUploadService.thumb(RequestUtils.siteId(), info.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                    info.setGoodsCoverThumbMid(cover);
                    return info;
                })
                .peek(o -> {
                    o.setStatSaleNum(Optional.ofNullable(o.getStatSaleNum()).orElse(0));
                    o.setStatCollectNum(Optional.ofNullable(o.getStatCollectNum()).orElse(0));
                    o.setStatEvaluateNum(Optional.ofNullable(o.getStatEvaluateNum()).orElse(0));
                    o.setStatAccessNum(Optional.ofNullable(o.getStatAccessNum()).orElse(0));
                    o.setSort(Optional.ofNullable(o.getSort()).orElse(0));
                    o.setGoodsSort(Optional.ofNullable(o.getGoodsSort()).orElse(0));
                })
                .sorted(Comparator
                        .<ShopGoodsRankGoodsListInfo>comparingInt(o -> JSONUtil.parseObj(o).getInt("stat" + StrUtil.upperFirst(rank.getRuleType()) + "Num")).reversed()
                        .thenComparingInt(ShopGoodsRankGoodsListInfo::getSort).reversed()
                        .thenComparingInt(ShopGoodsRankGoodsListInfo::getGoodsSort).reversed()
                        .thenComparingLong(o -> DateUtils.StringToTimestamp(o.getCreateTime())))
                .toList();
    }

    private ShopGoodsSku getGoodsSku(Integer goodsId) {
        QueryWrapper<ShopGoodsSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("goods_id", goodsId);
        List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(queryWrapper);
        return ObjectUtil.isEmpty(skuList) ? null : skuList.get(0);
    }
}
