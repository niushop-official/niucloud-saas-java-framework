package com.niu.shop.listener.marketing;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.enums.common.CommonActiveEnum;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.discount.ShopDiscount;
import com.niu.shop.entity.discount.ShopDiscountGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.manjian.ShopManjianGoods;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.MarketingActivityTypeEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.mapper.discount.ShopDiscountGoodsMapper;
import com.niu.shop.mapper.discount.ShopDiscountMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.manjian.ShopManjianGoodsMapper;
import com.niu.shop.mapper.manjian.ShopManjianMapper;
import com.niu.shop.service.admin.goods.IShopGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@EventCallback("shop")
public class GetGoodsJoinInfoListener extends CommonEventDefiner {

    @Resource
    private ShopGoodsMapper shopGoodsMapper;
    
    @Resource
    private IShopGoodsService shopGoodsService;

    @Resource
    private ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Resource
    private ShopActiveMapper shopActiveMapper;

    @Resource
    private ShopDiscountGoodsMapper shopDiscountGoodsMapper;

    @Resource
    private ShopDiscountMapper shopDiscountMapper;

    @Resource
    private ShopManjianGoodsMapper shopManjianGoodsMapper;

    @Resource
    private ShopManjianMapper shopManjianMapper;

    @Override
    public CommonEventResult handleCallback(CommonEvent event) {
        if (!"GetGoodsJoinInfo".equals(event.getEventName())) {
            return null;
        }

        Map<String, Object> eventData = (Map<String, Object>) event.getData();
        List<Integer> goodsIds = (List<Integer>) eventData.get("goods_ids");
        Integer siteId = Integer.parseInt(eventData.get("site_id").toString());
        Integer isGetCount = Integer.parseInt(eventData.getOrDefault("is_get_count", 0).toString());
        Integer isAll = Integer.parseInt(eventData.getOrDefault("is_all", 0).toString());
        Map<String, Object> where = (Map<String, Object>) eventData.getOrDefault("where", Map.of());

        QueryWrapper<ShopGoods> queryWrapper = new QueryWrapper<>();
        if (isAll == 1){
            QueryWrapper batchAllQuery = shopGoodsService.getBatchAllQuery(queryWrapper, where);
            List<ShopGoods> list = shopGoodsMapper.selectList(batchAllQuery);
            if (!CollectionUtils.isEmpty(list)){
                goodsIds = list.stream().map(ShopGoods::getGoodsId).collect(Collectors.toList());
            }
        }

        if (CollectionUtils.isEmpty(goodsIds)){
            return null;
        }

        List<ShopActiveGoods> shopActiveGoods = shopActiveGoodsMapper.selectList(new LambdaQueryWrapper<ShopActiveGoods>()
                .eq(ShopActiveGoods::getSiteId, siteId)
                .eq(ShopActiveGoods::getActiveGoodsStatus, "active")
                .in(ShopActiveGoods::getActiveGoodsType, ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType())
                .ne(ShopActiveGoods::getActiveClass, MarketingActivityTypeEnum.DISCOUNT.getType())
                .in(ShopActiveGoods::getGoodsId, goodsIds)
                .groupBy(ShopActiveGoods::getGoodsId, ShopActiveGoods::getActiveId));

        List<ShopDiscountGoods> shopDiscountGoods = shopDiscountGoodsMapper.selectList(new LambdaQueryWrapper<ShopDiscountGoods>()
                .eq(ShopDiscountGoods::getSiteId, siteId)
                .eq(ShopDiscountGoods::getStatus, "active")
                .in(ShopDiscountGoods::getGoodsId, goodsIds)
                .groupBy(ShopDiscountGoods::getGoodsId, ShopDiscountGoods::getDiscountId));

        List<ShopManjianGoods> shopManjianGoods = shopManjianGoodsMapper.selectList(new LambdaQueryWrapper<ShopManjianGoods>()
                .eq(ShopManjianGoods::getSiteId, siteId)
                .eq(ShopManjianGoods::getStatus, 1)
                .in(ShopManjianGoods::getGoodsId, goodsIds)
                .groupBy(ShopManjianGoods::getGoodsId, ShopManjianGoods::getManjianId));

        if (isGetCount == 1) {
            CommonEventResult result = new CommonEventResult();
            result.setResult(shopActiveGoods.size() + shopDiscountGoods.size() + shopManjianGoods.size());
            return result;
        }
        Set<Integer> activeIds = shopActiveGoods.stream().map(ShopActiveGoods::getActiveId).collect(Collectors.toSet());
        Map<Integer, String> shopActiveMap = CollectionUtils.isEmpty(activeIds) ? new HashMap<Integer, String>() :
                shopActiveMapper.selectByIds(activeIds).stream().collect(Collectors.toMap(ShopActive::getActiveId, ShopActive::getActiveName));

        Set<Integer> discountIds = shopDiscountGoods.stream().map(ShopDiscountGoods::getDiscountId).collect(Collectors.toSet());
        Map<Integer, String> shopDiscountMap = CollectionUtils.isEmpty(discountIds) ? new HashMap<Integer, String>() :
                shopDiscountMapper.selectByIds(discountIds).stream().collect(Collectors.toMap(ShopDiscount::getDiscountId, ShopDiscount::getName));

        Set<Integer> manjianIds = shopManjianGoods.stream().map(ShopManjianGoods::getManjianId).collect(Collectors.toSet());
        Map<Integer, String> shopManjianMap = CollectionUtils.isEmpty(manjianIds) ? new HashMap<Integer, String>() :
                shopManjianMapper.selectByIds(manjianIds).stream().collect(Collectors.toMap(ShopManjian::getManjianId, ShopManjian::getManjianName));

        Map<Integer, Map<String, Map<String, Object>>> returnMap = new HashMap<>();
        for (ShopActiveGoods shopActiveGood : shopActiveGoods) {
            Map<String, Object> activeShort = CommonActiveEnum.getActiveShort(shopActiveGood.getActiveClass());
            Map<String, Object> activityInfo = new HashMap<>();
            Integer activeId = shopActiveGood.getActiveId();
            activityInfo.put("join_id", activeId);
            activityInfo.put("join_type", "active_" + shopActiveGood.getActiveClass());
            activityInfo.put("short", activeShort);
            activityInfo.put("name", shopActiveMap.containsKey(activeId) ? shopActiveMap.get(activeId) : activeShort.getOrDefault("active_name", ""));

            // 按商品ID分组存储
            returnMap.computeIfAbsent(shopActiveGood.getGoodsId(), k -> new HashMap<>())
                    .put(shopActiveGood.getActiveClass() +"_" + activeId, activityInfo);
        }

        Map<String, Object> discountShort = CommonActiveEnum.getActiveShort(MarketingActivityTypeEnum.DISCOUNT.getType());
        for (ShopDiscountGoods shopDiscountGood : shopDiscountGoods) {
            Map<String, Object> discountInfo = new HashMap<>();
            Integer discountId = shopDiscountGood.getDiscountId();
            discountInfo.put("join_id", discountId);
            discountInfo.put("join_type", MarketingActivityTypeEnum.DISCOUNT.getType());
            discountInfo.put("short", discountShort);
            discountInfo.put("name", shopDiscountMap.containsKey(discountId) ? shopDiscountMap.get(discountId) : discountShort.getOrDefault("active_name", ""));

            returnMap.computeIfAbsent(shopDiscountGood.getGoodsId(), k -> new HashMap<>())
                    .put(MarketingActivityTypeEnum.DISCOUNT.getType() +"_" + discountId, discountInfo);
        }

        Map<String, Object> mamjiansongShort = CommonActiveEnum.getActiveShort(MarketingActivityTypeEnum.MANJIANSONG.getType());
        for (ShopManjianGoods shopManjianGood : shopManjianGoods) {
            Map<String, Object> manjianInfo = new HashMap<>();
            Integer manjianId = shopManjianGood.getManjianId();
            manjianInfo.put("join_id", manjianId);
            manjianInfo.put("join_type", MarketingActivityTypeEnum.MANJIANSONG.getType());
            manjianInfo.put("short", mamjiansongShort);
            manjianInfo.put("name", shopManjianMap.containsKey(manjianId) ? shopManjianMap.get(manjianId) : mamjiansongShort.getOrDefault("active_name", ""));

            returnMap.computeIfAbsent(shopManjianGood.getGoodsId(), k -> new HashMap<>())
                    .put(MarketingActivityTypeEnum.MANJIANSONG.getType() +"_" + manjianId, manjianInfo);

        }
        CommonEventResult result = new CommonEventResult();
        result.setResult(returnMap);
        return result;
    }
}