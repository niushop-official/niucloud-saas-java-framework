package com.niu.shop.service.api.marketing.impl;

import cn.binarywang.wx.miniapp.constant.WxMaApiUrlConstants;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.discount.ShopDiscount;
import com.niu.shop.entity.discount.ShopDiscountGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsLabel;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.mapper.discount.ShopDiscountGoodsMapper;
import com.niu.shop.mapper.discount.ShopDiscountMapper;
import com.niu.shop.mapper.goods.ShopGoodsLabelMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelListVo;
import com.niu.shop.service.admin.marketing.vo.ShopDiscountOrderListVo;
import com.niu.shop.service.api.marketing.IShopDiscountService;
import com.niu.shop.service.api.marketing.param.DiscountGoodsSearchParam;
import com.niu.shop.service.api.marketing.param.ShopDiscountListParam;
import com.niu.shop.service.api.marketing.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IShopDiscountServiceImpl implements IShopDiscountService {

    @Resource
    ShopDiscountMapper shopDiscountMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    ShopDiscountGoodsMapper shopDiscountGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Resource
    private ShopGoodsLabelMapper shopGoodsLabelMapper;

    @Override
    public ShopDiscountGoodsInfoVo getInfoByGoods(Integer goodsId) {
        ShopDiscountGoods shopActiveGoods = shopDiscountGoodsMapper.selectOne(new QueryWrapper<ShopDiscountGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("status", "active")
                .eq("goods_id", goodsId)
                .last("limit 1")
        );
        ShopDiscountGoodsInfoVo vo = new ShopDiscountGoodsInfoVo();
        if (shopActiveGoods != null) {
            BeanUtils.copyProperties(shopActiveGoods, vo);
            vo.setActiveGoodsId(shopActiveGoods.getDiscountGoodsId());
            vo.setActiveId(shopActiveGoods.getDiscountId());
            ShopDiscount shopDiscount = shopDiscountMapper.selectById(shopActiveGoods.getDiscountId());
            if (shopDiscount != null) {
                ShopDiscountGoodsInfoVo.Discount discount = new ShopDiscountGoodsInfoVo.Discount();
                BeanUtils.copyProperties(shopDiscount, discount);
                discount.setActiveName(shopDiscount.getName());
                discount.setActiveDesc(shopDiscount.getRemark());
                discount.setActiveId(shopDiscount.getDiscountId());
                vo.setActive(discount);
            }
            return vo;
        }
        return null;
    }

    /**
     * 限时折扣配置
     *
     * @return
     */
    @Override
    public JSONArray getDiscountBannerConfig() {
        return coreConfigService.getConfigArrayValue(RequestUtils.siteId(), "SHOP_DISCOUNT_BANNER_CONFIG");
    }

    /**
     * 获取活动商品列表
     *
     * @param pageParam
     * @param searchParam
     */
    @Override
    public PageResult<ShopDiscountGoodsListNewVo> getGoodsPage(PageParam pageParam, DiscountGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper();
        queryWrapper.setAlias("sg")
                .select("sg.site_id,sg.goods_id,sg.label_ids,sg.goods_name,sg.sub_title,sg.goods_category,sg.goods_type,sg.goods_cover,sg.unit,sg.status,sg.sale_num + sg.virtual_sale_num as sale_num,sg.member_discount,sg.is_discount,dg.discount_id,dg.status as discount_status")
                .leftJoin("?_shop_discount_goods as dg on sg.goods_id = dg.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("sg.site_id", RequestUtils.siteId())
                .eq("sg.status", 1)
                .and(ObjectUtil.isNotEmpty(searchParam.getKeyword()), wrapper ->
                        wrapper.like("sg.goods_name", searchParam.getKeyword())
                                .or().like("sg.sub_title", searchParam.getKeyword()))
                .eq(ObjectUtil.isNotEmpty(searchParam.getBrandId()), "sg.brand_id", searchParam.getBrandId())
                .in("dg.status", ActiveStatusEnum.NOT_ACTIVE.getStatus(), ActiveStatusEnum.ACTIVE.getStatus())
                .eq(ObjectUtil.isNotEmpty(searchParam.getDiscountId()), "dg.discount_id", searchParam.getDiscountId())
                .groupBy("sg.goods_id");
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory())) {
            QueryMapperUtils.buildJsonArraySearchCondition(queryWrapper, searchParam.getGoodsCategory(), "sg.goods_category");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds())) {
            QueryMapperUtils.buildJsonArraySearchCondition(queryWrapper, searchParam.getLabelIds(), "sg.label_ids");
        }
        if (ObjectUtil.isNotEmpty(searchParam.getServiceIds())) {
            QueryMapperUtils.buildJsonArraySearchCondition(queryWrapper, searchParam.getServiceIds(), "sg.service_ids");
        }
        String orderField = searchParam.getOrder();
        String sortDirection = searchParam.getSort();
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("sale_num", "sale_num");
        fieldMapping.put("sale_price", "dg.discount_price");
        if (StringUtils.isNotEmpty(orderField) && ("sale_num".equals(orderField) || "sale_price".equals(orderField))) {
            String dbField = fieldMapping.get(orderField);
            queryWrapper.orderBy(true, "asc".equalsIgnoreCase(sortDirection), dbField);
        } else {
            queryWrapper.orderByDesc("sg.sort").orderByDesc("sg.create_time");
        }

        // 执行查询
        IPage<ShopDiscountGoodsListNewVo> iPage = shopGoodsMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), ShopDiscountGoodsListNewVo.class, queryWrapper);

        if (CollectionUtils.isEmpty(iPage.getRecords())) {
            return PageResult.build(page, limit);
        }
        List<ShopDiscountGoodsListNewVo> list = iPage.getRecords();
        Set<Integer> goodsIds = list.stream().map(ShopDiscountGoodsListNewVo::getGoodsId).collect(Collectors.toSet());
        List<ShopGoodsSku> shopGoodsSkus = shopGoodsSkuMapper.selectList(new LambdaQueryWrapper<ShopGoodsSku>().in(ShopGoodsSku::getGoodsId, goodsIds));
        Map<Integer, List<ShopGoodsSku>> skuMap = shopGoodsSkus.stream().collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId));
        for (ShopDiscountGoodsListNewVo item : list) {
            if (StringUtils.isNotEmpty(item.getLabelIds()) && !"[]".equals(item.getLabelIds())) {
                List<Integer> labelIds = JSONUtil.toList(item.getLabelIds(), Integer.class);
                List<ShopGoodsLabel> shopGoodsLabels = shopGoodsLabelMapper.selectByIds(labelIds);
                if (CollectionUtils.isNotEmpty(shopGoodsLabels)) {
                    item.setGoodsLabelName(CollectUtils.convert(shopGoodsLabels, ShopDiscountGoodsListNewVo.GoodsLabel.class));
                }
            }
            if (skuMap.containsKey(item.getGoodsId())) {
                List<ShopGoodsSku> skuList = skuMap.get(item.getGoodsId());
                item.setSkuList(CollectUtils.convert(skuList, ShopDiscountGoodsListNewVo.SkuInfo.class));
                Map<Integer, ShopGoodsSku> skuIdMap = skuList.stream().collect(Collectors.toMap(ShopGoodsSku::getSkuId, Function.identity()));
                List<ShopDiscountGoods> shopDiscountGoods = shopDiscountGoodsMapper.selectList(new LambdaQueryWrapper<ShopDiscountGoods>()
                        .eq(ShopDiscountGoods::getDiscountId, item.getDiscountId())
                        .eq(ShopDiscountGoods::getGoodsId, item.getGoodsId()));
                if (CollectionUtils.isNotEmpty(shopDiscountGoods)) {
                    for (ShopDiscountGoods discountGoods : shopDiscountGoods) {
                        if (ObjectUtil.isNotEmpty(discountGoods.getIsEnabled())) {
                            ShopGoodsSku goodsSku = skuIdMap.get(discountGoods.getSkuId());
                            BigDecimal discountPrice = discountGoods.getDiscountPrice().compareTo(BigDecimal.ZERO) == 0 ? goodsSku.getPrice() : discountGoods.getDiscountPrice();
                            if (item.getGoodsSku() == null) {
                                ShopDiscountGoodsListNewVo.SkuInfo skuInfo = new ShopDiscountGoodsListNewVo.SkuInfo();
                                BeanUtils.copyProperties(goodsSku, skuInfo);
                                skuInfo.setDiscountType(discountGoods.getType());
                                skuInfo.setDiscountRate(discountGoods.getRate().compareTo(BigDecimal.ZERO) == 0 ? "111" : discountGoods.getRate().toString());
                                skuInfo.setDiscountPrice(discountPrice.toString());
                                skuInfo.setReduceMoney(discountGoods.getReduceMoney().toString());
                                item.setGoodsSku(skuInfo);
                            }
                        }
                    }
                }
                if (item.getGoodsSku() == null && CollectionUtils.isNotEmpty(skuList)) {
                    ShopDiscountGoodsListNewVo.SkuInfo skuInfo = new ShopDiscountGoodsListNewVo.SkuInfo();
                    if (CollectionUtils.isNotEmpty(skuList)) {
                        BeanUtils.copyProperties(skuList.get(0), skuInfo);
                    }
                    skuInfo.setDiscountType("discount");
                    skuInfo.setDiscountRate("10");
                    skuInfo.setDiscountPrice(skuList.get(0).getPrice().toString());
                    skuInfo.setReduceMoney("0");
                }
            }
        }

        return PageResult.build(page, limit, iPage.getTotal(), list);
    }


    /**
     * 限时折扣列表
     *
     * @param shopDiscountListParam
     * @return
     */
    @Override
    public List<ShopDiscountVo> getList(ShopDiscountListParam shopDiscountListParam) {
        QueryWrapper<ShopDiscount> queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .in("status", Arrays.asList("active", "not_active"));
        if (ObjectUtil.isNotEmpty(shopDiscountListParam.getActiveName())) {
            queryWrapper.like("name", shopDiscountListParam.getActiveName());
        }
        if (ObjectUtil.isNotEmpty(shopDiscountListParam.getLimit())) {
            queryWrapper.last("limit " + shopDiscountListParam.getLimit());
        }
        queryWrapper.orderByAsc("start_time");
        List<ShopDiscount> shopActiveList = shopDiscountMapper.selectList(queryWrapper);
        List<ShopDiscountVo> list = new LinkedList<>();
        for (ShopDiscount item : shopActiveList) {
            ShopDiscountVo vo = new ShopDiscountVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
