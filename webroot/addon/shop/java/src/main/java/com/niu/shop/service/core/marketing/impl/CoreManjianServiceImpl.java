package com.niu.shop.service.core.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.interfaces.MPJBaseJoin;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.manjian.ShopManjianGiveRecords;
import com.niu.shop.entity.manjian.ShopManjianGoods;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.enums.marketing.*;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.manjian.ShopManjianGiveRecordsMapper;
import com.niu.shop.mapper.manjian.ShopManjianGoodsMapper;
import com.niu.shop.mapper.manjian.ShopManjianMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.admin.goods.vo.ShopManjianRuleContentVo;
import com.niu.shop.service.api.cart.vo.ShopCartCalculateVo;
import com.niu.shop.service.api.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.core.goods.ICoreGoodsService;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.marketing.ICoreManjianStatService;
import com.niu.shop.service.core.marketing.ICoreShopManjianGiveRecordsService;
import com.niu.shop.service.core.marketing.param.GetManjianInfoParam;
import com.niu.shop.service.core.marketing.vo.ShopManjianAllInfoVo;
import com.niu.shop.service.core.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoreManjianServiceImpl extends ServiceImpl<ShopManjianMapper, ShopManjian> implements ICoreManjianService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    ShopManjianMapper shopManjianMapper;

    @Resource
    ShopManjianGoodsMapper shopManjianGoodsMapper;

    @Resource
    ShopCouponMapper shopCouponMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopManjianGiveRecordsMapper shopManjianGiveRecordsMapper;

    @Resource
    ICoreShopManjianGiveRecordsService coreShopManjianGiveRecordsService;

    @Resource
    ICoreMemberAccountService coreMemberAccount;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;

    @Resource
    ICoreManjianStatService coreManjianStatService;

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;

    @Resource
    ICoreGoodsService coreGoodsService;


    @Override
    public void calculate(final OrderCreateDataVo order) {
        MPJQueryWrapper queryWrapper = new MPJQueryWrapper<ShopManjian>().eq("site_id", order.getSiteId()).eq("status", ShopManjianStatusEnum.ACTIVE.getValue());
        queryWrapper.select("*");
        List<ShopManjianInfoVo> manjianList = shopManjianMapper.selectJoinList(ShopManjianInfoVo.class, queryWrapper);
        Member memberInfo = memberMapper.selectById(order.getMemberId());
        if (ObjectUtil.isEmpty(manjianList) || ObjectUtil.isEmpty(memberInfo)) return;

        String joinMemberType;
        JSONArray levelIds;
        JSONArray labelIds;
        Boolean canJoin = false;
        BigDecimal manjianDiscountMoney = BigDecimal.ZERO;
        //OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
        Map<Integer, OrderCreateDataVo.Discount> manjianDiscountMap = new HashMap<>();
        for (ShopManjianInfoVo manjianInfo : manjianList) {
            joinMemberType = manjianInfo.getJoinMemberType();
            levelIds = ObjectUtil.isNotEmpty(manjianInfo.getLevelIds()) ? JSONUtil.parseArray(manjianInfo.getLevelIds()) : new JSONArray();
            labelIds = ObjectUtil.isNotEmpty(manjianInfo.getLabelIds()) ? JSONUtil.parseArray(manjianInfo.getLabelIds()) : new JSONArray();
            if (joinMemberType.equals(ShopManjianMemberTypeEnum.ALL_MEMBER.getType())) {
                // 所有会员参与
                canJoin = true;
            } else if (joinMemberType.equals(ShopManjianMemberTypeEnum.SELECTED_MEMBER_LEVEL.getType())) {
                // 指定会员等级
                Integer memberLevel = memberInfo.getMemberLevel();
                if (levelIds.contains(memberLevel)) {
                    canJoin = true;
                }
            } else if (joinMemberType.equals(ShopManjianMemberTypeEnum.SELECTED_MEMBER_LABEL.getType())) {
                // 指定会员标签
                String memberLabels = memberInfo.getMemberLabel();
                if (ObjectUtil.isNotEmpty(memberLabels)) {
                    JSONArray memberLabelsArr = JSONUtil.parseArray(memberLabels);
                    for (Object label : memberLabelsArr) {
                        if (labelIds.contains(label)) {
                            canJoin = true;
                            break;
                        }
                    }
                }
            }
            if (!canJoin) continue;

            //找到true的满减规则，进行计算
            String conditionType = manjianInfo.getConditionType();
            BigDecimal conditionMatchValue = BigDecimal.ZERO;
            BigDecimal conditionMatchMoney = BigDecimal.ZERO;
            List<Integer> matchSkuIds = new ArrayList<>();
            BigDecimal matchOrderGoodsMoney = BigDecimal.ZERO;
            for (OrderCreateDataVo.Sku goods : order.getGoodsData().values()) {

                Long manjianGoodsCount = shopManjianGoodsMapper.selectCount(
                        new QueryWrapper<ShopManjianGoods>()
                                .eq("manjian_id", manjianInfo.getManjianId())
                                .eq("goods_id", goods.getGoodsId())
                                .eq("sku_id", goods.getSkuId())
                                .eq("site_id", order.getSiteId())
                                .eq("status", ShopManjianStatusEnum.ACTIVE.getValue()));

                if (manjianInfo.getGoodsType().equals(ShopManjianGoodsTypeEnum.ALL_GOODS.getValue())) {
                    if (conditionType.equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
                        conditionMatchValue = conditionMatchValue.add(goods.getPrice().multiply(new BigDecimal(goods.getNum())));
                    } else {
                        conditionMatchValue = conditionMatchValue.add(new BigDecimal(goods.getNum()));
                    }
                    conditionMatchMoney = conditionMatchMoney.add(goods.getPrice().multiply(new BigDecimal(goods.getNum())));
                    matchSkuIds.add(goods.getSkuId());
                    matchOrderGoodsMoney = matchOrderGoodsMoney.add(goods.getGoodsMoney().subtract(goods.getDiscountMoney()));
                } else if (manjianInfo.getGoodsType().equals(ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue())) {
                    if (manjianGoodsCount > 0) {
                        if (conditionType.equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
                            conditionMatchValue = conditionMatchValue.add(goods.getPrice().multiply(new BigDecimal(goods.getNum())));
                        } else {
                            conditionMatchValue = conditionMatchValue.add(new BigDecimal(goods.getNum()));
                        }
                        conditionMatchMoney = conditionMatchMoney.add(goods.getPrice().multiply(new BigDecimal(goods.getNum())));
                        matchSkuIds.add(goods.getSkuId());
                        matchOrderGoodsMoney = matchOrderGoodsMoney.add(goods.getGoodsMoney().subtract(goods.getDiscountMoney()));
                    }
                } else if (manjianInfo.getGoodsType().equals(ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue())) {
                    if (manjianGoodsCount == 0) {
                        if (conditionType.equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
                            conditionMatchValue = conditionMatchValue.add(goods.getPrice().multiply(new BigDecimal(goods.getNum())));
                        } else {
                            conditionMatchValue = conditionMatchValue.add(new BigDecimal(goods.getNum()));
                        }
                        conditionMatchMoney = conditionMatchMoney.add(goods.getPrice().multiply(new BigDecimal(goods.getNum())));
                        matchSkuIds.add(goods.getSkuId());
                        matchOrderGoodsMoney = matchOrderGoodsMoney.add(goods.getGoodsMoney().subtract(goods.getDiscountMoney()));
                    }
                }
            }

            BigDecimal discountMoney = BigDecimal.ZERO;
            boolean isFreeShipping = false;
            Map<String, Object> gift = new HashMap<>();
            Integer level = -1;

            JSONArray ruleJson = JSONUtil.parseArray(manjianInfo.getRuleJson());
            for (int i = 0; i < ruleJson.size(); i++) {
                JSONObject rule = ruleJson.getJSONObject(i);
                BigDecimal ruleLimit = new BigDecimal(rule.getStr("limit"));
                if (conditionMatchValue.compareTo(ruleLimit) >= 0) {
                    if (manjianInfo.getRuleType().equals(ShopManjianRuleTypeEnum.LADDER.getType())) {
                        Integer discountType = rule.getInt("discount_type");
                        if (discountType == 1) {
                            discountMoney = rule.getBigDecimal("discount_money", BigDecimal.ZERO);
                        } else {
                            discountMoney = conditionMatchMoney.multiply(BigDecimal.ONE.subtract(rule.getBigDecimal("discount_money", BigDecimal.ZERO).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
                        }

                        if (!rule.getBool("is_discount")) {
                            rule.put("discount_money", BigDecimal.ZERO);
                            discountMoney = BigDecimal.ZERO;
                        }
                        gift.put("point", rule.getBigDecimal("point", BigDecimal.ZERO));
                        if (!rule.getBool("is_give_point")) {
                            rule.put("point", BigDecimal.ZERO);
                            gift.put("point", BigDecimal.ZERO);
                        }
                        gift.put("balance", rule.getBigDecimal("balance", BigDecimal.ZERO));
                        if (!rule.getBool("is_give_balance")) {
                            rule.put("balance", BigDecimal.ZERO);
                            gift.put("balance", BigDecimal.ZERO);
                        }
                        JSONArray giftCoupon = rule.getJSONArray("coupon");
                        if (ObjectUtil.isNotEmpty(giftCoupon)) {
                            List<JSONObject> validGiftCoupon = new ArrayList<>();
                            for (Object couponObj : giftCoupon) {
                                JSONObject coupon = (JSONObject) couponObj;
                                ShopCoupon couponManjian = shopCouponMapper.selectById(coupon.getLong("coupon_id"));
                                if (ObjectUtil.isNotEmpty(couponManjian)) {
                                    if (couponManjian.getMinConditionMoney().compareTo(BigDecimal.ZERO) == 0) {
                                        coupon.put("coupon_name", couponManjian.getPrice() + "元无门槛券");
                                    } else {
                                        coupon.put("coupon_name", "满" + couponManjian.getMinConditionMoney() + "元减" + couponManjian.getPrice() + "元券");
                                    }
                                    validGiftCoupon.add(coupon);
                                }
                            }
                            rule.put("coupon", validGiftCoupon);
                            gift.put("coupon", validGiftCoupon);
                        }
                        if (!rule.getBool("is_give_coupon")) {
                            rule.put("coupon", new JSONArray());
                            gift.put("coupon", new JSONArray());
                        }
                        JSONArray giftGoods = rule.getJSONArray("goods");
                        if (ObjectUtil.isNotEmpty(giftGoods)) {
                            List<JSONObject> validGiftGoods = new ArrayList<>();
                            for (Object goodsObj : giftGoods) {
                                JSONObject goods = (JSONObject) goodsObj;

                                ShopGoodsSku goodsSkuManjian = shopGoodsSkuMapper.selectById(goods.getLong("sku_id"));
                                ShopGoods goodsManjian = shopGoodsMapper.selectById(goodsSkuManjian.getGoodsId());
                                if (ObjectUtil.isNotEmpty(goodsSkuManjian) && goodsManjian.getStatus() == 1) {
                                    goods.put("goods_name", goodsManjian.getGoodsName());
                                    goods.put("sku_name", goodsSkuManjian.getSkuName());
                                    goods.put("sku_image", goodsSkuManjian.getSkuImage());
                                    goods.put("price", goodsSkuManjian.getPrice());
                                    validGiftGoods.add(goods);
                                }
                            }
                            rule.put("goods", validGiftGoods);
                            gift.put("goods", validGiftGoods);
                        }
                        if (!rule.getBool("is_give_goods")) {
                            rule.put("goods", new JSONArray());
                            gift.put("goods", new JSONArray());
                        }
                    } else { // 循环优惠
                        int cycleNum = conditionMatchValue.divide(ruleLimit, 0, RoundingMode.FLOOR).intValue();
                        rule.put("limit", ruleLimit.multiply(new BigDecimal(cycleNum)));
                        if (rule.getBool("is_discount")) {
                            if (ruleLimit.compareTo(BigDecimal.ZERO) == 0) {
                                discountMoney = BigDecimal.ZERO;
                            } else {
                                discountMoney = rule.getBigDecimal("discount_money", BigDecimal.ZERO).multiply(new BigDecimal(cycleNum));
                            }
                            rule.put("discount_money", discountMoney);
                        }
                        if (rule.getBool("is_give_point")) {
                            rule.put("point", rule.getBigDecimal("point", BigDecimal.ZERO).multiply(new BigDecimal(cycleNum)));
                            gift.put("point", rule.getBigDecimal("point"));
                        }
                        if (rule.getBool("is_give_balance")) {
                            rule.put("balance", rule.getBigDecimal("balance", BigDecimal.ZERO).multiply(new BigDecimal(cycleNum)));
                            gift.put("balance", rule.getBigDecimal("balance"));
                        }
                        if (rule.getBool("is_give_coupon")) {
                            JSONArray giftCoupon = rule.getJSONArray("coupon");
                            if (ObjectUtil.isNotEmpty(giftCoupon)) {
                                List<JSONObject> updatedGiftCoupon = new ArrayList<>();
                                for (Object couponObj : giftCoupon) {
                                    JSONObject coupon = (JSONObject) couponObj;
                                    coupon.put("num", coupon.getInt("num") * cycleNum);
                                    ShopCoupon couponManjian = shopCouponMapper.selectById(coupon.getLong("coupon_id"));
                                    if (ObjectUtil.isNotEmpty(couponManjian)) {
                                        if (couponManjian.getMinConditionMoney().compareTo(BigDecimal.ZERO) == 0) {
                                            coupon.put("coupon_name", couponManjian.getPrice() + "元无门槛券");
                                        } else {
                                            coupon.put("coupon_name", "满" + couponManjian.getMinConditionMoney() + "元减" + couponManjian.getPrice() + "元券");
                                        }
                                        updatedGiftCoupon.add(coupon);
                                    }
                                }
                                rule.put("coupon", updatedGiftCoupon);
                                gift.put("coupon", updatedGiftCoupon);
                            }
                        }
                        if (rule.getBool("is_give_goods")) {
                            JSONArray giftGoods = rule.getJSONArray("goods");
                            if (ObjectUtil.isNotEmpty(giftGoods)) {
                                List<JSONObject> updatedGiftGoods = new ArrayList<>();
                                for (Object goodsObj : giftGoods) {
                                    JSONObject goods = (JSONObject) goodsObj;
                                    goods.put("num", goods.getInt("num") * cycleNum);
                                    ShopGoodsSku goodsSkuManjian = shopGoodsSkuMapper.selectById(goods.getLong("sku_id"));
                                    ShopGoods goodsManjian = shopGoodsMapper.selectById(goodsSkuManjian.getGoodsId());
                                    if (ObjectUtil.isNotEmpty(goodsManjian) && goodsManjian.getStatus() == 1) {
                                        goods.put("goods_name", goodsManjian.getGoodsName());
                                        goods.put("sku_name", goodsSkuManjian.getSkuName());
                                        goods.put("sku_image", goodsSkuManjian.getSkuImage());
                                        goods.put("price", goodsSkuManjian.getPrice());
                                        updatedGiftGoods.add(goods);
                                    }
                                }
                                rule.put("goods", updatedGiftGoods);
                                gift.put("goods", updatedGiftGoods);
                            }
                        }
                    }
                    isFreeShipping = rule.getBool("is_free_shipping", false);
                    level = i;
                }
            }

            manjianInfo.setRuleJson(ruleJson.toString());
            if (level >= 0) {
                manjianInfo.setRule(ruleJson.get(level).toString());
            }
            BigDecimal manjianOneDiscountMoney = BigDecimal.ZERO;
            if (level >= 0) {
                ShopManjianInfoVo shopManjianInfoVo = new ShopManjianInfoVo();
                BeanUtil.copyProperties(manjianInfo, shopManjianInfoVo);
                manjianOneDiscountMoney = manjianGoodsCalculate(order, shopManjianInfoVo, isFreeShipping, level, discountMoney, matchSkuIds, matchOrderGoodsMoney, manjianOneDiscountMoney);
            }
            if (manjianOneDiscountMoney.compareTo(BigDecimal.ZERO) > 0) {

                OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                discount.setMatchGoodsIds(matchSkuIds.toArray(new Integer[0]));
                discount.setNum(matchSkuIds.size());
                discount.setType("discount");
                discount.setMoney(manjianOneDiscountMoney);
                discount.setDiscountType("manjian");
                discount.setDiscountTypeId(manjianInfo.getManjianId());
                discount.setTitle(manjianInfo.getManjianName());
                discount.setContent("");

                manjianDiscountMap.put(manjianInfo.getManjianId(), discount);
                manjianDiscountMoney = manjianDiscountMoney.add(manjianOneDiscountMoney);
            }
            if (ObjectUtil.isNotEmpty(gift)) {
                getGiftContent(order, gift, manjianInfo.getManjianId(), level, matchSkuIds);
            }
        }
        if (manjianDiscountMoney.compareTo(BigDecimal.ZERO) > 0) {

            //添加满减map不太合适(填上多条满减数据)
            Map<String, Map<Integer, OrderCreateDataVo.Discount>> discountList = new HashMap<>();
            discountList.put("manjian", manjianDiscountMap);
            if (ObjectUtil.isEmpty(order.getDiscountList())) {
                order.setDiscountList(discountList);
            } else {
                order.getDiscountList().put("manjian", manjianDiscountMap);
            }


            OrderCreateDataVo.Basic basic = order.getBasic();
            basic.setDiscountMoney(basic.getDiscountMoney().add(manjianDiscountMoney));
            basic.setManjianDiscountMoney(manjianDiscountMoney);
            order.setBasic(basic);

        }
    }

    @Override
    public BigDecimal manjianGoodsCalculate(OrderCreateDataVo order, ShopManjianInfoVo manjianInfo, boolean isFreeShipping, int level, BigDecimal discountMoney, List<Integer> matchSkuIds, BigDecimal matchOrderGoodsMoney, BigDecimal manjianOneDiscountMoney) {

        if (discountMoney.compareTo(matchOrderGoodsMoney) > 0) {
            discountMoney = matchOrderGoodsMoney;
        }
        BigDecimal surplusMoney = new BigDecimal(discountMoney.toString());
        List<Integer> validSkuIds = new ArrayList<>();
        for (Integer skuId : matchSkuIds) {
            OrderCreateDataVo.Sku sku = order.getGoodsData().get(skuId);
            if (sku.getGoodsMoney().subtract(sku.getDiscountMoney()).compareTo(BigDecimal.ZERO) != 0) {
                validSkuIds.add(skuId);
            }
        }
        int matchCount = validSkuIds.size();
        for (int i = 0; i < matchCount; i++) {
            Integer skuId = validSkuIds.get(i);
            OrderCreateDataVo.Sku sku = order.getGoodsData().get(skuId);
            BigDecimal itemOrderGoodsMoney = sku.getGoodsMoney().subtract(sku.getDiscountMoney());

            if (isFreeShipping) {
                sku.getGoods().setIsFreeShipping(1);
                sku.getGoods().setDeliveryMoney(BigDecimal.ZERO);
            }

            JSONObject rule = JSONUtil.parseArray(manjianInfo.getRuleJson()).getJSONObject(level);

            if (rule.getBool("is_discount") || rule.getBool("is_free_shipping") || rule.getBool("is_give_point") || rule.getBool("is_give_balance") || rule.getJSONArray("coupon").size() > 0 || rule.getJSONArray("goods").size() > 0) {
                sku.setManjianInfo(manjianInfo);
            }

            // 订单项满减优惠计算
            BigDecimal itemDiscountMoney;
            if (i == (matchCount - 1)) {
                itemDiscountMoney = surplusMoney;
            } else {
                if (matchOrderGoodsMoney.compareTo(BigDecimal.ZERO) == 0 || discountMoney.compareTo(BigDecimal.ZERO) == 0) {
                    itemDiscountMoney = BigDecimal.ZERO;
                } else {
                    itemDiscountMoney = itemOrderGoodsMoney.divide(matchOrderGoodsMoney, 2, RoundingMode.HALF_UP).multiply(discountMoney);
                    if (itemDiscountMoney.compareTo(BigDecimal.ZERO) == 0) {
                        itemDiscountMoney = itemOrderGoodsMoney;
                    }
                    if (itemDiscountMoney.compareTo(surplusMoney) > 0) {
                        itemDiscountMoney = surplusMoney;
                    }
                }
            }

            sku.setDiscountMoney(sku.getDiscountMoney().add(itemDiscountMoney));
            manjianOneDiscountMoney = manjianOneDiscountMoney.add(itemDiscountMoney);
            surplusMoney = surplusMoney.subtract(itemDiscountMoney);
        }
        return manjianOneDiscountMoney;
    }

    @Override
    public void getGiftContent(OrderCreateDataVo order, Map<String, Object> gift, Integer manjianId, int level, List<Integer> skuIds) {

        Map<String, Object> newGift = new HashMap<>();
        for (Map.Entry<String, Object> entry : gift.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key) {
                case "balance":
                case "point":
                    if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0) {
                        newGift.put(key, value);
                    }
                    break;
                case "coupon":
                    JSONArray couponArray = JSONUtil.parseArray(value);
                    if (ObjectUtil.isNotEmpty(couponArray)) {
                        List<JSONObject> validCoupons = new ArrayList<>();
                        for (int i = 0; i < couponArray.size(); i++) {
                            JSONObject coupon = couponArray.getJSONObject(i);
                            ShopCoupon couponObj = shopCouponMapper.selectById(coupon.getLong("coupon_id"));
                            if (couponObj != null && couponObj.getStatus() == 1) {
                                validCoupons.add(coupon);
                            }
                        }
                        if (!validCoupons.isEmpty()) {
                            newGift.put(key, validCoupons);
                        }
                    }


                    break;
                case "goods":
                    JSONArray goodsArray = JSONUtil.parseArray(value);
                    List<JSONObject> validGoods = new ArrayList<>();
                    Map<Integer, OrderCreateDataVo.Sku> giftData = new HashMap<>();
                    for (int i = 0; i < goodsArray.size(); i++) {
                        JSONObject goods = goodsArray.getJSONObject(i);
                        ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectById(goods.getLong("sku_id"));
                        ShopGoods goodsObj = shopGoodsMapper.selectById(goodsSku.getGoodsId());
                        if (goodsSku != null && goodsObj.getStatus() == 1 && goodsSku.getStock() >= goods.getInt("num", 0)) {
                            goods.put("goods_name", goodsObj.getGoodsName());
                            goods.put("sku_name", goodsSku.getSkuName());
                            goods.put("sku_image", goodsSku.getSkuImage());
                            goods.put("price", goodsSku.getPrice());
                            validGoods.add(goods);

                            //添加赠品
                            OrderCreateDataVo.Sku skuVo = new OrderCreateDataVo.Sku();
                            goodsSku.setMemberPrice(null);
                            BeanUtil.copyProperties(goodsSku, skuVo);
                            skuVo.setOriginalPrice(goodsSku.getPrice());
                            skuVo.setPrice(BigDecimal.ZERO);
                            skuVo.setNum(goods.getInt("num", 1));
                            skuVo.setGoodsMoney(BigDecimal.ZERO);
                            skuVo.setDiscountMoney(BigDecimal.ZERO);
                            skuVo.setIsGift(1);
                            OrderCreateDataVo.Goods goodsVo = new OrderCreateDataVo.Goods();
                            BeanUtil.copyProperties(goodsObj, goodsVo);
                            skuVo.setGoods(goodsVo);
                            giftData.put(skuVo.getSkuId(), skuVo);
                        }
                    }
                    order.setGiftGoods(giftData);
                    if (!validGoods.isEmpty()) {
                        newGift.put(key, validGoods);
                    }
                    break;
            }
        }

        Map<String, Object> manjianGift = new HashMap<>();
        Map<String, Object> manjianInfo = new HashMap<>();
        manjianInfo.put("gift", newGift);
        manjianInfo.put("level", level);
        manjianInfo.put("sku_ids", skuIds);
        manjianGift.put(manjianId.toString(), manjianInfo);
        Map<String, Map<String, Object>> giftMap = order.getGift();
        if (ObjectUtil.isEmpty(giftMap)) {
            giftMap = new HashMap<>();
        }
        giftMap.put("manjian", manjianGift);
        order.setGift(giftMap);
    }

    @Override
    public boolean addGiveRecords(OrderCreateDataVo order) {
        // 从订单信息中获取满减活动相关的赠送数据
        if (ObjectUtil.isEmpty(order.getGift())) return true;
        Map<String, Object> manjianGiftMap = order.getGift().getOrDefault("manjian", new HashMap<>());
        if (manjianGiftMap.isEmpty()) {
            return true;
        }
        log.info("订单创建后满减计算");
        log.info(manjianGiftMap.toString());
        List<ShopManjianGiveRecords> giveRecordsList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : manjianGiftMap.entrySet()) {
            Integer manjianId = Integer.parseInt(entry.getKey());
            Map<String, Object> manjianInfo = (Map<String, Object>) entry.getValue();

            Map<String, Object> gift = (Map<String, Object>) manjianInfo.get("gift");
            Integer level = (Integer) manjianInfo.get("level");
            List<Integer> skuIds = (List<Integer>) manjianInfo.get("sku_ids");

            List<JSONObject> couponJsonList = new ArrayList<>();
            if (gift.containsKey("coupon") && gift.get("coupon") instanceof JSONArray couponArray) {
                for (Object couponObj : couponArray) {
                    JSONObject couponJson = (JSONObject) couponObj;
                    couponJsonList.add(couponJson);
                }
            }

            List<JSONObject> goodsJsonList = new ArrayList<>();
            if (gift.containsKey("goods") && gift.get("goods") instanceof JSONArray goodsArray) {
                for (Object goodsObj : goodsArray) {
                    JSONObject goodsJson = (JSONObject) goodsObj;
                    goodsJsonList.add(goodsJson);
                }
            }

            ShopManjianGiveRecords giveRecord = new ShopManjianGiveRecords();
            giveRecord.setSiteId(order.getSiteId());
            giveRecord.setMemberId(order.getMemberId());
            giveRecord.setOrderId(order.getOrderId());
            giveRecord.setManjianId(manjianId);
            giveRecord.setLevel(level);
            giveRecord.setPoint((Integer) gift.getOrDefault("point", 0));
            giveRecord.setBalance(new BigDecimal(gift.getOrDefault("balance", BigDecimal.ZERO).toString()));
            giveRecord.setCouponJson(JSONUtil.toJsonStr(couponJsonList));
            giveRecord.setGoodsJson(JSONUtil.toJsonStr(goodsJsonList));
            giveRecord.setSkuIds(skuIds.toString());

            giveRecordsList.add(giveRecord);

            //添加会员参与次数
            Long count = shopManjianGiveRecordsMapper.selectCount(new QueryWrapper<ShopManjianGiveRecords>().eq("site_id", order.getSiteId()).eq("member_id", order.getMemberId()).eq("manjian_id", manjianId));
            if (ObjectUtil.isEmpty(count)) {
                count = 0L;
            }
            Map<String, String> statData = new HashMap<>();
            statData.put("order_num", "1");
            statData.put("member_num", count == 0L ? "1" : "0");
            coreManjianStatService.stat(order.getSiteId(), manjianId, statData);

        }

        if (ObjectUtil.isNotEmpty(giveRecordsList)) {
            return coreShopManjianGiveRecordsService.saveOrUpdateBatch(giveRecordsList);
        }
        return true;
    }

    @Override
    public boolean giftGrant(ShopOrder order) {
        // 查询满减活动赠送记录，构建查询条件并指定要查询的字段
        QueryWrapper<ShopManjianGiveRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", order.getSiteId())
                .eq("order_id", order.getOrderId())
                .eq("member_id", order.getMemberId())
                .select("site_id", "member_id", "manjian_id", "point", "balance", "coupon_json", "goods_json");
        List<ShopManjianGiveRecords> giveRecords = shopManjianGiveRecordsMapper.selectList(queryWrapper);

        if (ObjectUtil.isNotEmpty(giveRecords)) {

            Integer couponNum = 0;
            for (ShopManjianGiveRecords v : giveRecords) {
                // 会员积分发放
                if (v.getPoint() > 0) {
                    coreMemberAccount.addLog(v.getSiteId(), v.getMemberId(), "point", (double) v.getPoint(), "manjian_gift_give", "满减送活动赠送", v.getManjianId().toString());
                }
                // 会员余额发放
                if (v.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    coreMemberAccount.addLog(v.getSiteId(), v.getMemberId(), "balance", v.getBalance().doubleValue(), "manjian_gift_give", "满减送活动赠送", v.getManjianId().toString());
                }
                if (ObjectUtil.isNotEmpty(v.getCouponJson())) {
                    JSONArray couponArray = JSONUtil.parseArray(v.getCouponJson());
                    for (int i = 0; i < couponArray.size(); i++) {
                        JSONObject coupon = couponArray.getJSONObject(i);
                        try {
                            coreCouponMemberService.sendCoupon(v.getSiteId(), v.getMemberId(), coupon.getInt("coupon_id"), coupon.getInt("num"));
                            couponNum += coupon.getInt("num");
                        } catch (Exception e) {
                            log.error("满减优惠券发放失败" + e);
                        }
                    }
                }
                Integer goodsNum = 0;
                if (ObjectUtil.isNotEmpty(v.getGoodsJson())) {
                    JSONArray goodsArray = JSONUtil.parseArray(v.getGoodsJson());
                    int[] nums = new int[goodsArray.size()];
                    for (int i = 0; i < goodsArray.size(); i++) {
                        JSONObject goods = goodsArray.getJSONObject(i);
                        nums[i] = goods.getInt("num");
                    }

                    for (int num : nums) {
                        goodsNum += num;
                    }
                }

                // 满减活动数据统计
                Map<String, String> statData = new HashMap<>();
                statData.put("order_money", order.getOrderMoney().toString());
                statData.put("point", v.getPoint().toString());
                statData.put("balance", v.getBalance().toString());
                statData.put("coupon_num", couponNum.toString());
                statData.put("goods_num", goodsNum.toString());
                coreManjianStatService.stat(v.getSiteId(), v.getManjianId(), statData);
            }
        }
        return true;
    }

    @Override
    public Map<Integer, JSONObject> refundCheck(ShopOrderRefund order) {
        // 查询订单商品信息（除了指定的商品），构建查询条件并指定要查询的字段
        QueryWrapper<ShopOrderGoods> orderGoodsQueryWrapper = new QueryWrapper<>();
        orderGoodsQueryWrapper.eq("order_id", order.getOrderId())
                .eq("member_id", order.getMemberId())
                .ne("order_goods_id", order.getOrderGoodsId())
                .eq("is_gift", 0)
                .select("order_goods_id", "site_id", "goods_id", "sku_id", "num", "goods_money", "extend");
        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsMapper.selectList(orderGoodsQueryWrapper);
        Map<Integer, ShopOrderGoods> orderGoodsMap = new HashMap<>();
        for (ShopOrderGoods goods : orderGoodsList) {
            orderGoodsMap.put(goods.getOrderGoodsId(), goods);
        }

        // 查询满减活动赠送记录，构建查询条件并指定要查询的字段
        QueryWrapper<ShopManjianGiveRecords> giveRecordsQueryWrapper = new QueryWrapper<>();
        giveRecordsQueryWrapper.eq("site_id", order.getSiteId())
                .eq("order_id", order.getOrderId())
                .eq("member_id", order.getMemberId())
                .select("site_id", "manjian_id", "level", "point", "balance", "coupon_json", "goods_json");
        List<ShopManjianGiveRecords> giveRecords = shopManjianGiveRecordsMapper.selectList(giveRecordsQueryWrapper);

        Map<Integer, JSONObject> refundGiftList = new HashMap<>();
        for (ShopManjianGiveRecords value : giveRecords) {
            // 根据满减活动ID等条件查询满减活动信息
            QueryWrapper<ShopManjian> manjianQueryWrapper = new QueryWrapper<>();
            manjianQueryWrapper.eq("site_id", value.getSiteId())
                    .eq("manjian_id", value.getManjianId());
            ShopManjian manjianInfo = shopManjianMapper.selectOne(manjianQueryWrapper);
            if (manjianInfo == null) {
                continue;
            }

            String conditionType = manjianInfo.getConditionType();
            int conditionMatchValue = 0;

            for (ShopOrderGoods v : orderGoodsList) {
                // 查询满减活动商品关联记录数量
                long manjianGoodsCount = shopManjianGoodsMapper.selectCount(new QueryWrapper<ShopManjianGoods>()
                        .eq("site_id", v.getSiteId())
                        .eq("goods_id", v.getGoodsId())
                        .eq("sku_id", v.getSkuId())
                        .eq("manjian_id", value.getManjianId())
                        .eq("status", ShopManjianStatusEnum.ACTIVE.getValue()));
                if (manjianInfo.getGoodsType().equals(ShopManjianGoodsTypeEnum.ALL_GOODS.getValue())) {
                    if (conditionType.equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
                        conditionMatchValue += v.getGoodsMoney().intValue();
                    } else {
                        conditionMatchValue += v.getNum();
                    }
                } else if (manjianInfo.getGoodsType().equals(ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue())) {
                    if (manjianGoodsCount > 0) {
                        if (conditionType.equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
                            conditionMatchValue += v.getGoodsMoney().intValue();
                        } else {
                            conditionMatchValue += v.getNum();
                        }
                    }
                } else if (manjianInfo.getGoodsType().equals(ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue())) {
                    if (manjianGoodsCount == 0) {
                        if (conditionType.equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
                            conditionMatchValue += v.getGoodsMoney().intValue();
                        } else {
                            conditionMatchValue += v.getNum();
                        }
                    }
                }
            }

            JSONObject gift = new JSONObject();
            int level = -1;
            String ruleType = manjianInfo.getRuleType();
            JSONArray ruleJson = JSONUtil.parseArray(manjianInfo.getRuleJson());
            for (int key = 0; key < ruleJson.size(); key++) {
                JSONObject rule = ruleJson.getJSONObject(key);
                int ruleLimit = rule.getInt("limit");
                if (conditionMatchValue >= ruleLimit) {
                    if (ruleType.equals(ShopManjianRuleTypeEnum.LADDER.getType())) {
                        // 积分
                        gift.put("point", rule.getBigDecimal("point", BigDecimal.ZERO).intValue());
                        if (!rule.getBool("is_give_point")) {
                            gift.put("point", 0);
                        }
                        // 余额
                        gift.put("balance", rule.getBigDecimal("balance", BigDecimal.ZERO).intValue());
                        if (!rule.getBool("is_give_balance")) {
                            gift.put("balance", 0);
                        }
                        // 优惠券
                        JSONArray couponArray = rule.getJSONArray("coupon");
                        gift.put("coupon", couponArray);
                        if (!rule.getBool("is_give_coupon")) {
                            gift.put("coupon", new JSONArray());
                        }
                        // 赠品
                        JSONArray goodsArray = rule.getJSONArray("goods");
                        gift.put("goods", goodsArray);
                        if (!rule.getBool("is_give_goods")) {
                            gift.put("goods", new JSONArray());
                        }
                    } else { // 循环优惠
                        int cycleNum = conditionMatchValue / ruleLimit;
                        if (rule.getBool("is_give_point")) {
                            gift.put("point", rule.getBigDecimal("point", BigDecimal.ZERO).intValue() * cycleNum);
                        }
                        if (rule.getBool("is_give_balance")) {
                            gift.put("balance", rule.getBigDecimal("balance", BigDecimal.ZERO).intValue() * cycleNum);
                        }
                        if (rule.getBool("is_give_coupon")) {
                            JSONArray couponArray = rule.getJSONArray("coupon");
                            if (ObjectUtil.isNotEmpty(couponArray)) {
                                for (int i = 0; i < couponArray.size(); i++) {
                                    JSONObject coupon = couponArray.getJSONObject(i);
                                    coupon.put("num", coupon.getInt("num") * cycleNum);
                                }
                            }
                            gift.put("coupon", couponArray);
                        }
                        if (rule.getBool("is_give_goods")) {
                            JSONArray goodsArray = rule.getJSONArray("goods");
                            if (ObjectUtil.isNotEmpty(goodsArray)) {
                                for (int i = 0; i < goodsArray.size(); i++) {
                                    JSONObject goods = goodsArray.getJSONObject(i);
                                    goods.put("num", goods.getInt("num") * cycleNum);
                                }
                            }
                            gift.put("goods", goodsArray);
                        }
                    }
                    level = key;
                }
            }

            if (level < value.getLevel()) { // 优惠层级降级，退还全部赠品
                if (value.getPoint() > 0) {
                    gift.put("point", value.getPoint());
                }
                if (value.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    gift.put("balance", value.getBalance());
                }
                if (ObjectUtil.isNotEmpty(value.getCouponJson())) {
                    gift.put("coupon", JSONUtil.parseArray(value.getCouponJson()));
                }
                if (ObjectUtil.isNotEmpty(value.getGoodsJson())) {
                    gift.put("goods", JSONUtil.parseArray(value.getGoodsJson()));
                }
            } else {
                if (ruleType.equals(ShopManjianRuleTypeEnum.CYCLE.getType())) {
                    if (value.getPoint() > 0) {
                        if (ObjectUtil.isNotEmpty(gift) && gift.containsKey("point")) {
                            int giftPoint = gift.getInt("point");
                            if (value.getPoint() > giftPoint) {
                                gift.put("point", value.getPoint() - giftPoint);
                            }
                        }
                    }
                    if (value.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                        if (ObjectUtil.isNotEmpty(gift) && gift.containsKey("balance")) {
                            BigDecimal giftBalance = gift.getBigDecimal("balance");
                            if (value.getBalance().compareTo(giftBalance) > 0) {
                                gift.put("balance", value.getBalance().subtract(giftBalance));
                            }
                        }
                    }
                    if (ObjectUtil.isNotEmpty(value.getCouponJson())) {
                        JSONArray valueCouponArray = JSONUtil.parseArray(value.getCouponJson());
                        JSONArray giftCouponArray = gift.getJSONArray("coupon");
                        if (ObjectUtil.isNotEmpty(giftCouponArray)) {
                            List<JSONObject> remainingCoupons = new ArrayList<>();
                            for (Object couponObj : valueCouponArray) {
                                JSONObject coupon = (JSONObject) couponObj;
                                int couponNum = coupon.getInt("num");
                                for (Object giftCouponObj : giftCouponArray) {
                                    JSONObject giftCoupon = (JSONObject) giftCouponObj;
                                    int giftCouponNum = giftCoupon.getInt("num");
                                    if (couponNum > giftCouponNum) {
                                        coupon.put("num", couponNum - giftCouponNum);
                                        remainingCoupons.add(coupon);
                                    }
                                }
                            }
                            gift.put("coupon", remainingCoupons);
                        }
                    }
                    if (ObjectUtil.isNotEmpty(value.getGoodsJson())) {
                        JSONArray valueGoodsArray = JSONUtil.parseArray(value.getGoodsJson());
                        JSONArray giftGoodsArray = gift.getJSONArray("goods");
                        if (ObjectUtil.isNotEmpty(giftGoodsArray)) {
                            List<JSONObject> remainingGoods = new ArrayList<>();
                            for (Object goodsObj : valueGoodsArray) {
                                JSONObject goods = (JSONObject) goodsObj;
                                int goodsNum = goods.getInt("num");
                                for (Object giftGoodsObj : giftGoodsArray) {
                                    JSONObject giftGoods = (JSONObject) giftGoodsObj;
                                    int giftGoodsNum = giftGoods.getInt("num");
                                    if (goodsNum > giftGoodsNum) {
                                        goods.put("num", goodsNum - giftGoodsNum);
                                        remainingGoods.add(goods);
                                    }
                                }
                            }
                            gift.put("goods", remainingGoods);
                        }
                    }
                }
            }

            if (gift.size() > 0) {
                refundGiftList.put(value.getManjianId(), gift);
            }
        }

        return refundGiftList;
    }

    @Override
    public boolean refundGift(ShopOrderRefund order) {
        Map<Integer, JSONObject> refundGiftList = refundCheck(order);
        if (ObjectUtil.isNotEmpty(refundGiftList)) {
            for (Map.Entry<Integer, JSONObject> entry : refundGiftList.entrySet()) {
                Integer manjianId = entry.getKey();
                JSONObject v = entry.getValue();

                // 构建查询满减活动赠送记录的条件
                QueryWrapper<ShopManjianGiveRecords> giveRecordsQueryWrapper = new QueryWrapper<>();
                giveRecordsQueryWrapper.eq("site_id", order.getSiteId())
                        .eq("member_id", order.getMemberId())
                        .eq("order_id", order.getOrderId())
                        .eq("manjian_id", manjianId);
                ShopManjianGiveRecords giveRecords = shopManjianGiveRecordsMapper.selectOne(giveRecordsQueryWrapper);

                // 构建查询会员信息的条件，获取会员的积分、余额信息
                QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
                memberQueryWrapper.eq("member_id", order.getMemberId())
                        .eq("site_id", order.getSiteId())
                        .select("point", "balance");
                Member memberInfo = memberMapper.selectOne(memberQueryWrapper);

                // 处理会员积分扣除逻辑
                if (v.containsKey("point") && v.getInt("point") > 0) {
                    if (memberInfo != null && memberInfo.getPoint() >= v.getInt("point")) {
                        coreMemberAccount.addLog(order.getSiteId(), order.getMemberId(), "point", (double) v.getInt("point") * (-1), "manjian_gift_back", "满减送活动订单退款退还赠送积分", manjianId.toString());
                        ShopManjianGiveRecords updateRecord = new ShopManjianGiveRecords();
                        updateRecord.setPoint(memberInfo.getPoint() - v.getInt("point"));
                        giveRecordsQueryWrapper = new QueryWrapper<>();
                        giveRecordsQueryWrapper.eq("site_id", order.getSiteId())
                                .eq("member_id", order.getMemberId())
                                .eq("order_id", order.getOrderId())
                                .eq("manjian_id", manjianId);
                        shopManjianGiveRecordsMapper.update(updateRecord, giveRecordsQueryWrapper);
                    }
                }

                // 处理会员余额扣除逻辑
                if (v.containsKey("balance") && v.getBigDecimal("balance") != null && v.getBigDecimal("balance").compareTo(BigDecimal.ZERO) > 0) {
                    if (memberInfo != null && memberInfo.getBalance().compareTo(v.getBigDecimal("balance")) >= 0) {
                        coreMemberAccount.addLog(order.getSiteId(), order.getMemberId(), "balance", -v.getBigDecimal("balance").doubleValue(), "manjian_gift_back", "满减送活动订单退款退还赠送余额", manjianId.toString());
                        ShopManjianGiveRecords updateRecord = new ShopManjianGiveRecords();
                        updateRecord.setBalance(memberInfo.getBalance().subtract(v.getBigDecimal("balance")));
                        giveRecordsQueryWrapper = new QueryWrapper<>();
                        giveRecordsQueryWrapper.eq("site_id", order.getSiteId())
                                .eq("member_id", order.getMemberId())
                                .eq("order_id", order.getOrderId())
                                .eq("manjian_id", manjianId);
                        shopManjianGiveRecordsMapper.update(updateRecord, giveRecordsQueryWrapper);
                    }
                }

                // 处理会员优惠券失效逻辑
                if (v.containsKey("coupon") && ObjectUtil.isNotEmpty(v.getJSONArray("coupon")) && giveRecords != null && giveRecords.getCouponJson() != null) {
                    JSONArray couponJson = new JSONArray();
                    JSONArray couponArray = v.getJSONArray("coupon");
                    for (int i = 0; i < couponArray.size(); i++) {
                        JSONObject coupon = couponArray.getJSONObject(i);
                        QueryWrapper<ShopCoupon> couponQueryWrapper = new QueryWrapper<>();
                        couponQueryWrapper.eq("id", coupon.getInt("coupon_id"))
                                .eq("site_id", order.getSiteId());
                        ShopCoupon couponInfo = shopCouponMapper.selectOne(couponQueryWrapper);
                        if (ObjectUtil.isNotEmpty(couponInfo)) {
                            QueryWrapper<ShopCouponMember> couponMemberQueryWrapper = new QueryWrapper<>();
                            couponMemberQueryWrapper.eq("site_id", order.getSiteId())
                                    .eq("member_id", order.getMemberId())
                                    .eq("coupon_id", coupon.getInt("coupon_id"))
                                    .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                                    .orderByDesc("create_time")
                                    .last("LIMIT " + coupon.getInt("num"));
                            List<Integer> ids = shopCouponMemberMapper.selectList(couponMemberQueryWrapper).stream().map(ShopCouponMember::getId).collect(Collectors.toList());
                            if (ObjectUtil.isNotEmpty(ids)) {
                                coreCouponMemberService.invalid(ids);
                                couponInfo.setReceiveCount(couponInfo.getReceiveCount() - coupon.getInt("num"));
                                //暂时不对当前剩余数量处理，满减减少不对当前数量处理
                                /*                  if (couponInfo.getRemainCount()!= -1) {
                                    couponInfo.setRemainCount(couponInfo.getRemainCount() + coupon.getInt("num"));
                                }*/
                                shopCouponMapper.updateById(couponInfo);
                            }
                            JSONArray giveRecordCouponArray = JSONUtil.parseArray(giveRecords.getCouponJson());
                            if (ObjectUtil.isNotEmpty(giveRecordCouponArray)) {
                                Map<Integer, Integer> couponNumMap = new HashMap<>();
                                for (Object obj : giveRecordCouponArray) {
                                    JSONObject jsonObj = (JSONObject) obj;
                                    couponNumMap.put(jsonObj.getInt("coupon_id"), jsonObj.getInt("num"));
                                }
                                if (couponNumMap.containsKey(coupon.getInt("coupon_id")) && couponNumMap.get(coupon.getInt("coupon_id")) > coupon.getInt("num")) {
                                    JSONObject remainingCoupon = new JSONObject();
                                    remainingCoupon.set("coupon_id", coupon.getInt("coupon_id"));
                                    remainingCoupon.set("num", couponNumMap.get(coupon.getInt("coupon_id")) - coupon.getInt("num"));
                                    couponJson.add(remainingCoupon);
                                }
                            }
                        }
                    }
                    ShopManjianGiveRecords updateRecord = new ShopManjianGiveRecords();
                    updateRecord.setCouponJson(couponJson.toString());
                    giveRecordsQueryWrapper = new QueryWrapper<>();
                    giveRecordsQueryWrapper.eq("site_id", order.getSiteId())
                            .eq("member_id", order.getMemberId())
                            .eq("order_id", order.getOrderId())
                            .eq("manjian_id", manjianId);
                    shopManjianGiveRecordsMapper.update(updateRecord, giveRecordsQueryWrapper);
                }

                // 处理赠品退款或扣除赠送数量逻辑
                if (v.containsKey("goods") && ObjectUtil.isNotEmpty(v.getJSONArray("goods")) && giveRecords != null && giveRecords.getGoodsJson() != null) {
                    JSONArray goodsJson = new JSONArray();
                    JSONArray goodsArray = v.getJSONArray("goods");
                    for (int i = 0; i < goodsArray.size(); i++) {
                        JSONObject goods = goodsArray.getJSONObject(i);
                        QueryWrapper<ShopOrderGoods> orderGoodsQueryWrapper = new QueryWrapper<>();
                        orderGoodsQueryWrapper.eq("site_id", order.getSiteId())
                                .eq("member_id", order.getMemberId())
                                .eq("order_id", order.getOrderId())
                                .eq("goods_id", goods.getInt("goods_id"))
                                .eq("sku_id", goods.getInt("sku_id"))
                                .eq("is_gift", 1);
                        ShopOrderGoods orderGoodsInfo = shopOrderGoodsMapper.selectOne(orderGoodsQueryWrapper);
                        if (ObjectUtil.isNotEmpty(orderGoodsInfo)) {
                            if (goods.getInt("num") >= orderGoodsInfo.getNum()) {
                                ShopOrderGoods updateGoods = new ShopOrderGoods();
                                updateGoods.setStatus(OrderGoodsRefundEnum.REFUND_FINISH.getStatus());
                                updateGoods.setIsEnableRefund(0);
                                orderGoodsQueryWrapper = new QueryWrapper<>();
                                orderGoodsQueryWrapper.eq("site_id", order.getSiteId())
                                        .eq("member_id", order.getMemberId())
                                        .eq("order_id", order.getOrderId())
                                        .eq("goods_id", goods.getInt("goods_id"))
                                        .eq("sku_id", goods.getInt("sku_id"))
                                        .eq("is_gift", 1);
                                shopOrderGoodsMapper.update(updateGoods, orderGoodsQueryWrapper);
                            } else {
                                ShopOrderGoods updateGoods = new ShopOrderGoods();
                                updateGoods.setNum(orderGoodsInfo.getNum() - goods.getInt("num"));
                                orderGoodsQueryWrapper = new QueryWrapper<>();
                                orderGoodsQueryWrapper.eq("site_id", order.getSiteId())
                                        .eq("member_id", order.getMemberId())
                                        .eq("order_id", order.getOrderId())
                                        .eq("goods_id", goods.getInt("goods_id"))
                                        .eq("sku_id", goods.getInt("sku_id"))
                                        .eq("is_gift", 1);
                                shopOrderGoodsMapper.update(updateGoods, orderGoodsQueryWrapper);
                            }
                            coreGoodsService.incStock(order.getSiteId(), goods.getInt("goods_id"), goods.getInt("sku_id"), goods.getInt("num"));
                        }
                        JSONArray giveRecordGoodsArray = JSONUtil.parseArray(giveRecords.getGoodsJson());
                        if (ObjectUtil.isNotEmpty(giveRecordGoodsArray)) {
                            Map<Integer, Integer> goodsNumMap = new HashMap<>();
                            for (Object obj : giveRecordGoodsArray) {
                                JSONObject jsonObj = (JSONObject) obj;
                                goodsNumMap.put(jsonObj.getInt("sku_id"), jsonObj.getInt("num"));
                            }
                            if (goodsNumMap.containsKey(goods.getInt("sku_id")) && goodsNumMap.get(goods.getInt("sku_id")) > goods.getInt("num")) {
                                JSONObject remainingGoods = new JSONObject();
                                remainingGoods.set("goods_id", goods.getInt("goods_id"));
                                remainingGoods.set("sku_id", goods.getInt("sku_id"));
                                remainingGoods.set("num", goodsNumMap.get(goods.getInt("sku_id")) - goods.getInt("num"));
                                goodsJson.add(remainingGoods);
                            }
                        }
                    }
                    ShopManjianGiveRecords updateRecord = new ShopManjianGiveRecords();
                    updateRecord.setGoodsJson(goodsJson.toString());
                    giveRecordsQueryWrapper = new QueryWrapper<>();
                    giveRecordsQueryWrapper.eq("site_id", order.getSiteId())
                            .eq("member_id", order.getMemberId())
                            .eq("order_id", order.getOrderId())
                            .eq("manjian_id", manjianId);
                    shopManjianGiveRecordsMapper.update(updateRecord, giveRecordsQueryWrapper);
                }

                // 满减活动数据统计
                Map<String, String> statData = new HashMap<>();
                statData.put("order_money", String.valueOf(order.getMoney().negate().toString()));
                statData.put("point", String.valueOf(-v.getInt("point", 0)));
                statData.put("balance", String.valueOf(-v.getBigDecimal("balance", BigDecimal.ZERO).doubleValue()));
                statData.put("coupon_num", String.valueOf(-v.getJSONArray("coupon").stream().mapToInt(coupon -> JSONUtil.parseObj(coupon).getInt("num")).sum()));
                statData.put("goods_num", String.valueOf(-v.getJSONArray("goods").stream().mapToInt(goods -> JSONUtil.parseObj(goods).getInt("num")).sum()));
                coreManjianStatService.stat(order.getSiteId(), manjianId, statData);
            }
        }
        return true;
    }

    @Override
    public void getOrderGoodsGiveInfo(ShopOrderInfoVo.OrderGoods orderGoodsInfo, List<ShopOrderInfoVo.OrderGoods> orderGoodsList, Integer siteId, Integer memberId) {

        QueryWrapper<ShopManjianGiveRecords> giveRecordsQueryWrapper = new QueryWrapper<>();
        giveRecordsQueryWrapper.select("manjian_id", "level", "point", "balance", "coupon_json", "goods_json", "sku_ids")
                .eq("site_id", siteId)
                .eq("member_id", memberId)
                .eq("order_id", orderGoodsInfo.getOrderId());
        List<ShopManjianGiveRecords> manjianRecords = shopManjianGiveRecordsMapper.selectList(giveRecordsQueryWrapper);
        List<ShopManjian> shopManjians = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(manjianRecords)) {
            for (ShopManjianGiveRecords vv : manjianRecords) {

                QueryWrapper<ShopManjian> manjianInfoQueryWrapper = new QueryWrapper<>();
                manjianInfoQueryWrapper.select("manjian_id", "manjian_name", "condition_type", "goods_type", "join_member_type", "rule_type", "rule_json")
                        .eq("site_id", siteId)
                        .eq("manjian_id", vv.getManjianId());
                List<ShopManjian> manjianList = shopManjianMapper.selectList(manjianInfoQueryWrapper);
                for (ShopManjian manjianInfo : manjianList) {
                    Integer conditionMatchValue = 0;
                    if (ObjectUtil.isNotEmpty(orderGoodsList)) {
                        for (ShopOrderInfoVo.OrderGoods v : orderGoodsList) {
                            if (v.getIsGift() == 0) {
                                if (vv.getSkuIds().contains(v.getSkuId().toString())) {
                                    if (ObjectUtil.isNotEmpty(manjianInfo)) {
                                        if (manjianInfo.getConditionType().equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
                                            conditionMatchValue += v.getGoodsMoney().intValue();
                                        } else {
                                            conditionMatchValue += v.getNum();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (vv.getSkuIds().contains(orderGoodsInfo.getSkuId().toString())) {
                        if (ObjectUtil.isNotEmpty(manjianInfo) && ObjectUtil.isNotEmpty(manjianInfo.getRuleJson())) {
                            JSONArray ruleJsonArray = JSONUtil.parseArray(manjianInfo.getRuleJson());
                            JSONObject ruleJson = ruleJsonArray.getJSONObject(vv.getLevel());
                            int cycleNum = 0;
                            if (manjianInfo.getRuleType().equals(ShopManjianRuleTypeEnum.CYCLE.getType())) {
                                cycleNum = conditionMatchValue / ruleJson.getInt("limit");
                                ruleJson.put("limit", ruleJson.getInt("limit") * cycleNum);
                                if (ObjectUtil.isNotEmpty(ruleJson.getBigDecimal("discount_money"))) {
                                    ruleJson.put("discount_money", BigDecimal.valueOf(cycleNum).multiply(ruleJson.getBigDecimal("discount_money")));
                                }
                                if (ObjectUtil.isNotEmpty(ruleJson.getBigDecimal("point"))) {
                                    ruleJson.put("point", ruleJson.getBigDecimal("point").intValue() * cycleNum);
                                }
                                if (ObjectUtil.isNotEmpty(ruleJson.getBigDecimal("balance"))) {
                                    ruleJson.put("balance", BigDecimal.valueOf(cycleNum).multiply(ruleJson.getBigDecimal("balance")));
                                }
                            }

                            if (ObjectUtil.isNotEmpty(ruleJson.getJSONArray("coupon"))) {
                                JSONArray couponArray = ruleJson.getJSONArray("coupon");
                                for (int i = 0; i < couponArray.size(); i++) {
                                    JSONObject coupon = couponArray.getJSONObject(i);
                                    if (manjianInfo.getRuleType().equals(ShopManjianRuleTypeEnum.CYCLE.getType()) && cycleNum > 0) {
                                        coupon.put("num", coupon.getInt("num") * cycleNum);
                                    }
                                    // 根据优惠券ID查询优惠券详细信息，用于获取名称等展示相关内容
                                    ShopCoupon couponInfo = shopCouponMapper.selectById(coupon.getLong("coupon_id"));
                                    if (ObjectUtil.isNotEmpty(couponInfo)) {
                                        if (couponInfo.getMinConditionMoney().compareTo(BigDecimal.ZERO) == 0) {
                                            coupon.put("coupon_name", couponInfo.getPrice() + "元无门槛券");
                                        } else {
                                            coupon.put("coupon_name", "满" + couponInfo.getMinConditionMoney() + "元减" + couponInfo.getPrice() + "元券");
                                        }
                                    }
                                }
                            }

                            if (ObjectUtil.isNotEmpty(ruleJson.getJSONArray("goods"))) {
                                JSONArray goodsArray = ruleJson.getJSONArray("goods");
                                for (int i = 0; i < goodsArray.size(); i++) {
                                    JSONObject goods = goodsArray.getJSONObject(i);
                                    if (manjianInfo.getRuleType().equals(ShopManjianRuleTypeEnum.CYCLE.getType()) && cycleNum > 0) {
                                        goods.put("num", goods.getInt("num") * cycleNum);
                                    }
                                    // 根据商品SKU ID查询商品SKU详细信息以及关联的商品信息，用于获取商品名称、价格等展示相关内容
                                    ShopGoodsSku skuInfo = shopGoodsSkuMapper.selectById(goods.getLong("sku_id"));

                                    if (ObjectUtil.isNotEmpty(skuInfo)) {
                                        ShopGoods goodsInfo = shopGoodsMapper.selectById(skuInfo.getGoodsId());
                                        goods.put("goods_name", goodsInfo.getGoodsName());
                                        goods.put("sku_name", skuInfo.getSkuName());
                                        goods.put("sku_image", skuInfo.getSkuImage());
                                        goods.put("price", skuInfo.getPrice());
                                    }
                                }
                            }

                            manjianInfo.setRuleJson(ruleJson.toString());
                            if (ruleJson.getBool("is_discount") || ruleJson.getBool("is_free_shipping") || ruleJson.getBool("is_give_point") || ruleJson.getBool("is_give_balance") || ruleJson.getBool("is_give_coupon") || ruleJson.getBool("is_give_goods")) {
                                shopManjians.add(manjianInfo);
                            }
                        }
                    }
                }
            }
        }
        orderGoodsInfo.setManjianInfo(shopManjians);
    }

    @Override
    public ShopCartCalculateVo manjianPromotion(ShopCartCalculateVo data, Integer siteId, Integer memberId) {

        BigDecimal promotionMoney = BigDecimal.ZERO;
        // 先查询全部商品的满减套餐  进行中
        QueryWrapper<ShopManjian> allInfoQueryWrapper = new QueryWrapper<>();
        allInfoQueryWrapper.eq("site_id", siteId)
                .eq("goods_type", ShopManjianGoodsTypeEnum.ALL_GOODS.getValue())
                .eq("status", ShopManjianStatusEnum.ACTIVE.getValue());
        ShopManjian allInfo = shopManjianMapper.selectOne(allInfoQueryWrapper);
        JSONObject manjianInfo = new JSONObject();
        List<ShopCartCalculateVo.Goods> goodsList = data.getGoodsList();
        // 存在全场满减(不考虑部分满减情况)
        if (allInfo != null) {
            // 验证当前用户是否可参与活动
            boolean canJoin = canJoinManjian(allInfo, siteId, memberId);
            if (canJoin) {
                // 获取折扣价格
                Map<String, String> discountArray = getManjianDiscountMoney(allInfo, data);
                ShopManjianAllInfoVo shopManjianAllInfoVo = new ShopManjianAllInfoVo();
                BeanUtil.copyProperties(allInfo, shopManjianAllInfoVo);
                shopManjianAllInfoVo.setDiscountArray(discountArray);
                BigDecimal discountMoney = new BigDecimal(discountArray.get("discount_money"));
                promotionMoney = promotionMoney.add(discountMoney);
                if (discountArray.containsKey("rule")) {
                    goodsList = goodsList.stream().map(item -> {
                        manjianInfo.clear();
                        manjianInfo.put("manjian", shopManjianAllInfoVo);
                        if (ObjectUtil.isNotEmpty(item.getPromotion())) {
                            item.getPromotion().put("manjian", manjianInfo);
                        } else {
                            item.setPromotion(new HashMap<>());
                            item.getPromotion().put("manjian", manjianInfo);
                        }


                        return item;
                    }).collect(Collectors.toList());
                }
            }
        } else {
            QueryWrapper<ShopManjian> notSelectInfoQueryWrapper = new QueryWrapper<>();
            notSelectInfoQueryWrapper.eq("site_id", siteId)
                    .eq("goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue())
                    .eq("status", ShopManjianStatusEnum.ACTIVE.getValue());
            ShopManjian notSelectInfo = shopManjianMapper.selectOne(notSelectInfoQueryWrapper);

            if (ObjectUtil.isNotEmpty(notSelectInfo)) {

                List<ShopManjianGoods> activeGoods = shopManjianGoodsMapper.selectList(new QueryWrapper<ShopManjianGoods>().eq("manjian_id", notSelectInfo.getManjianId()));
                // 验证当前用户是否可参与活动
                boolean canJoin = canJoinManjian(notSelectInfo, siteId, memberId);
                if (canJoin) {
                    List<Integer> notSelectGoodsSkuId = new ArrayList<>();
                    ShopCartCalculateVo itemGoodsData = new ShopCartCalculateVo();
                    itemGoodsData.setGoodsMoney(BigDecimal.ZERO);
                    itemGoodsData.setGoodsNum(0);
                    List<ShopCartCalculateVo.Goods> itemGoodsList = new ArrayList<>();
                    List<Integer> skuIds = new ArrayList<>();
                    BigDecimal discountMoney = BigDecimal.ZERO;

                    if (activeGoods != null && !activeGoods.isEmpty()) {
                        notSelectGoodsSkuId = activeGoods.stream().map(ShopManjianGoods::getSkuId).collect(Collectors.toList());
                    }

                    for (ShopCartCalculateVo.Goods goodsItem : goodsList) {
                        if (!notSelectGoodsSkuId.contains(goodsItem.getSkuId())) {
                            itemGoodsData.setGoodsMoney(itemGoodsData.getGoodsMoney().add(goodsItem.getGoodsMoney()));
                            itemGoodsData.setGoodsNum(itemGoodsData.getGoodsNum() + goodsItem.getNum());
                            itemGoodsList.add(goodsItem);
                            skuIds.add(goodsItem.getSkuId());
                        }
                    }
                    Map<String, String> discountArray = getManjianDiscountMoney(notSelectInfo, itemGoodsData);
                    discountMoney = new BigDecimal(discountArray.get("discount_money"));
                    promotionMoney = promotionMoney.add(discountMoney);
                    if (discountArray.containsKey("rule")) {
                        goodsList = goodsList.stream().map(item -> {
                            if (skuIds.contains(item.getSkuId())) {
                                manjianInfo.clear();
                                manjianInfo.put("manjian", notSelectInfo);
                                if (ObjectUtil.isNotEmpty(item.getPromotion())) {
                                    item.getPromotion().put("manjian", notSelectInfo);
                                } else {
                                    item.setPromotion(new HashMap<>());
                                    item.getPromotion().put("manjian", notSelectInfo);
                                }
                            }
                            return item;
                        }).collect(Collectors.toList());
                    }
                }
            } else {
                List<Integer> goodsIds = goodsList.stream().map(ShopCartCalculateVo.Goods::getGoodsId).distinct().collect(Collectors.toList());
                QueryWrapper<ShopManjianGoods> manjianGoodsQueryWrapper = new QueryWrapper<>();
                manjianGoodsQueryWrapper.eq("site_id", siteId)
                        .eq("status", ShopManjianStatusEnum.ACTIVE.getValue())
                        .in("goods_id", goodsIds);
                List<Integer> manjianGoodsList = shopManjianGoodsMapper.selectList(manjianGoodsQueryWrapper).stream().map(ShopManjianGoods::getManjianId).collect(Collectors.toList());
                if (!manjianGoodsList.isEmpty()) {
                    BigDecimal discountMoney = BigDecimal.ZERO;
                    List<Integer> manjianIds = manjianGoodsList.stream().distinct().collect(Collectors.toList());
                    Collections.sort(manjianIds);
                    QueryWrapper<ShopManjian> manjianListQueryWrapper = new QueryWrapper<>();
                    manjianListQueryWrapper.eq("site_id", siteId)
                            .eq("status", ShopManjianStatusEnum.ACTIVE.getValue())
                            .in("manjian_id", manjianIds)
                            .eq("goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue());
                    List<ShopManjian> manjianList = shopManjianMapper.selectList(manjianListQueryWrapper);
                    for (ShopManjian v : manjianList) {
                        // 验证当前用户是否可参与活动
                        boolean canJoin = canJoinManjian(v, siteId, memberId);
                        if (!canJoin) {
                            continue;
                        }
                        List<Integer> manjianGoodsSkuIds = new ArrayList<>();
                        List<ShopManjianGoods> activeGoods = shopManjianGoodsMapper.selectList(new QueryWrapper<ShopManjianGoods>().eq("manjian_id", v.getManjianId()));
                        if (activeGoods != null && !activeGoods.isEmpty()) {
                            manjianGoodsSkuIds = activeGoods.stream().map(ShopManjianGoods::getSkuId).collect(Collectors.toList());
                        }
                        ShopCartCalculateVo itemGoodsData = new ShopCartCalculateVo();
                        itemGoodsData.setGoodsMoney(BigDecimal.ZERO);
                        itemGoodsData.setGoodsNum(0);
                        List<ShopCartCalculateVo.Goods> itemGoodsList = new ArrayList<>();
                        List<Integer> skuIds = new ArrayList<>();
                        for (ShopCartCalculateVo.Goods goodsItem : goodsList) {
                            if (manjianGoodsSkuIds.contains(goodsItem.getSkuId())) {
                                itemGoodsData.setGoodsMoney(itemGoodsData.getGoodsMoney().add(goodsItem.getGoodsMoney()));
                                itemGoodsData.setGoodsNum(itemGoodsData.getGoodsNum() + goodsItem.getNum());
                                itemGoodsList.add(goodsItem);
                                skuIds.add(goodsItem.getSkuId());
                            }
                        }
                        Map<String, String> discountArray = getManjianDiscountMoney(v, itemGoodsData);
                        discountMoney = discountMoney.add(new BigDecimal(discountArray.get("discount_money")));
                        if (discountArray.containsKey("rule")) {
                            goodsList = goodsList.stream().map(item -> {
                                if (skuIds.contains(item.getSkuId())) {
                                    manjianInfo.clear();
                                    manjianInfo.put("manjian", v);
                                    manjianInfo.put("discount_array", discountArray);
                                    if (ObjectUtil.isNotEmpty(item.getPromotion())) {
                                        item.getPromotion().put("manjian", manjianInfo);
                                    } else {
                                        item.setPromotion(new HashMap<>());
                                        item.getPromotion().put("manjian", manjianInfo);
                                    }

                                }
                                return item;
                            }).collect(Collectors.toList());
                        }
                    }
                    promotionMoney = promotionMoney.add(discountMoney);
                }
            }
        }
        data.setGoodsList(goodsList);
        data.setPromotionMoney(promotionMoney);
        return data;
    }

    @Override
    public Map<String, String> getManjianDiscountMoney(ShopManjian manjianInfo, ShopCartCalculateVo data) {

        Map<String, String> result = new HashMap<>();
        BigDecimal discountMoney = BigDecimal.ZERO;
        BigDecimal goodsMoney = data.getGoodsMoney();
        BigDecimal goodsNum = new BigDecimal(data.getGoodsNum());
        BigDecimal value;
        String conditionType = manjianInfo.getConditionType();
        if (conditionType.equals(ShopManjianDiscountTypeEnum.OVER_N_YUAN.getType())) {
            // 按金额匹配条件时，取商品总金额
            value = goodsMoney;
        } else {
            // 按数量匹配条件时，取商品总数量
            value = goodsNum;
        }

        JSONArray ruleJson = JSONUtil.parseArray(manjianInfo.getRuleJson());
        // 用于记录符合条件的优惠规则
        JSONObject rule = null;
        Integer level = -1;
        if (manjianInfo.getRuleType().equals(ShopManjianRuleTypeEnum.LADDER.getType())) {
            for (int i = 0; i < ruleJson.size(); i++) {
                JSONObject currentRule = ruleJson.getJSONObject(i);
                BigDecimal ruleLimit = currentRule.getBigDecimal("limit");
                if (value.compareTo(ruleLimit) >= 0) {
                    rule = currentRule;
                    if (currentRule.containsKey("is_discount") && currentRule.getBool("is_discount")) {
                        if (currentRule.getInt("discount_type") == 1) {
                            // 减类型的折扣金额处理
                            discountMoney = currentRule.getBigDecimal("discount_money", BigDecimal.ZERO);
                        } else {
                            // 折类型的折扣金额处理，按公式计算折扣后的金额差值作为优惠金额
                            discountMoney = goodsMoney.subtract(goodsMoney.multiply(currentRule.getBigDecimal("discount_money", BigDecimal.ZERO).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
                        }
                    }
                    level = i;
                }
            }
        } else if (manjianInfo.getRuleType().equals(ShopManjianRuleTypeEnum.CYCLE.getType())) {
            for (int i = 0; i < ruleJson.size(); i++) {
                JSONObject currentRule = ruleJson.getJSONObject(i);
                BigDecimal ruleLimit = currentRule.getBigDecimal("limit");
                if (value.compareTo(ruleLimit) >= 0) {
                    rule = currentRule;
                    int cycleNum = value.divide(ruleLimit, 0, RoundingMode.FLOOR).intValue();
                    if (currentRule.containsKey("is_discount") && currentRule.getBool("is_discount")) {
                        if (ruleLimit.compareTo(BigDecimal.ZERO) == 0) {
                            discountMoney = BigDecimal.ZERO;
                        } else {
                            discountMoney = currentRule.getBigDecimal("discount_money", BigDecimal.ZERO).multiply(new BigDecimal(cycleNum));
                        }
                    }
                    level = i;
                }
            }
        }

        result.put("discount_money", discountMoney.toString());
        result.put("money", rule != null ? rule.getStr("limit", "0") : "0");
        result.put("rule", rule != null ? rule.toString() : "{}");
        result.put("level", level.toString());
        return result;
    }


    @Override
    public boolean canJoinManjian(ShopManjian manjianInfo, Integer siteId, Integer memberId) {

        boolean canJoin = false;
        // 获取满减规则中的参与会员类型、会员等级ID列表、会员标签ID列表
        String joinMemberType = manjianInfo.getJoinMemberType();
        JSONArray levelIds = ObjectUtil.isNotEmpty(manjianInfo.getLevelIds()) ? JSONUtil.parseArray(manjianInfo.getLevelIds()) : new JSONArray();
        JSONArray labelIds = ObjectUtil.isNotEmpty(manjianInfo.getLabelIds()) ? JSONUtil.parseArray(manjianInfo.getLabelIds()) : new JSONArray();
        Member memberInfo = memberMapper.selectById(memberId);

        if (joinMemberType.equals(ShopManjianMemberTypeEnum.ALL_MEMBER.getType())) {
            // 所有会员参与
            canJoin = true;
        } else if (joinMemberType.equals(ShopManjianMemberTypeEnum.SELECTED_MEMBER_LEVEL.getType())) {
            // 指定会员等级
            Integer memberLevel = memberInfo.getMemberLevel();
            if (levelIds.contains(memberLevel)) {
                canJoin = true;
            }
        } else if (joinMemberType.equals(ShopManjianMemberTypeEnum.SELECTED_MEMBER_LABEL.getType())) {
            // 指定会员标签
            String memberLabels = memberInfo.getMemberLabel();
            if (ObjectUtil.isNotEmpty(memberLabels)) {
                JSONArray memberLabelsArr = JSONUtil.parseArray(memberLabels);
                for (Object label : memberLabelsArr) {
                    if (labelIds.contains(label)) {
                        canJoin = true;
                        break;
                    }
                }
            }
        }
        return canJoin;
    }

    @Override
    public BigDecimal moneyFormat(BigDecimal money) {
        return null;
    }

    @Override
    public ShopManjianRuleContentVo getManjianInfo(GetManjianInfoParam param) {
        Integer skuId = param.getSkuId();
        if (ObjectUtil.isEmpty(param.getSkuId()) && ObjectUtil.isNotEmpty(param.getGoodsId())) {
            QueryWrapper<ShopGoodsSku> skuQueryWrapper = new QueryWrapper<>();
            skuQueryWrapper.eq("goods_id", param.getGoodsId())
                    .eq("site_id", param.getSiteId())
                    .eq("is_default", 1);
            ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(skuQueryWrapper);
            if (ObjectUtil.isNotEmpty(sku)) {
                skuId = sku.getSkuId();
            }
        }

        QueryWrapper<ShopManjian> manjianQueryWrapper = new QueryWrapper<>();
        manjianQueryWrapper.eq("site_id", param.getSiteId())
                .eq("status", ShopManjianStatusEnum.ACTIVE.getValue())
                .eq("goods_type", ShopManjianGoodsTypeEnum.ALL_GOODS.getValue())
                .le("start_time", System.currentTimeMillis() / 1000)
                .gt("end_time", System.currentTimeMillis() / 1000);
        List<ShopManjian> shopManjians = shopManjianMapper.selectList(manjianQueryWrapper);
        if (ObjectUtil.isNotEmpty(shopManjians)) {
            ShopManjian manjian = shopManjians.get(0);
            if (canJoinManjian(manjian, param.getSiteId(), param.getMemberId())) {
                ShopManjianRuleContentVo ruleContent = getRuleContent(manjian, param.getSiteId(), param.getMemberId());
                if (ruleContent.isJoin()) {
                    return ruleContent;
                }
            }

            return null;
        }

        manjianQueryWrapper.clear();
        manjianQueryWrapper.eq("site_id", param.getSiteId())
                .eq("status", ShopManjianStatusEnum.ACTIVE.getValue())
                .eq("goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue())
                .le("start_time", System.currentTimeMillis() / 1000)
                .gt("end_time", System.currentTimeMillis() / 1000);
        List<ShopManjian> manjianInfoSelectedGoodsNot = shopManjianMapper.selectList(manjianQueryWrapper);

        QueryWrapper<ShopManjianGoods> manjianGoodsQueryWrapper = new QueryWrapper<>();
        manjianGoodsQueryWrapper.eq("site_id", param.getSiteId())
                .eq("goods_id", param.getGoodsId())
                .eq("sku_id", skuId)
                .eq("status", ShopManjianStatusEnum.ACTIVE.getValue());
        List<ShopManjianGoods> manjianGoodsInfo = shopManjianGoodsMapper.selectList(manjianGoodsQueryWrapper);

        if (ObjectUtil.isNotEmpty(manjianInfoSelectedGoodsNot) && ObjectUtil.isEmpty(manjianGoodsInfo)) {
            ShopManjian manjian = manjianInfoSelectedGoodsNot.get(0);
            if (canJoinManjian(manjian, param.getSiteId(), param.getMemberId())) {
                ShopManjianRuleContentVo ruleContent = getRuleContent(manjian, param.getSiteId(), param.getMemberId());
                if (ruleContent.isJoin()) {
                    return ruleContent;
                }

                return null;
            }
        } else {
            manjianQueryWrapper.clear();
            manjianQueryWrapper.eq("site_id", param.getSiteId())
                    .eq("status", ShopManjianStatusEnum.ACTIVE.getValue())
                    .eq("goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue())
                    .le("start_time", System.currentTimeMillis() / 1000)
                    .gt("end_time", System.currentTimeMillis() / 1000);
            manjianInfoSelectedGoodsNot = shopManjianMapper.selectList(manjianQueryWrapper);

            QueryWrapper<ShopManjianGoods> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.eq("site_id", param.getSiteId())
                    .eq("goods_id", param.getGoodsId())
                    .eq("sku_id", skuId)
                    .eq("status", ShopManjianStatusEnum.ACTIVE.getValue());
            List<ShopManjianGoods> shopManjianGoods = shopManjianGoodsMapper.selectList(goodsQueryWrapper);

            if (ObjectUtil.isNotEmpty(manjianInfoSelectedGoodsNot) && ObjectUtil.isEmpty(shopManjianGoods)) {
                ShopManjian manjian = manjianInfoSelectedGoodsNot.get(0);
                if (canJoinManjian(manjian, param.getSiteId(), param.getMemberId())) {
                    ShopManjianRuleContentVo ruleContent = getRuleContent(manjian, param.getSiteId(), param.getMemberId());
                    if (ruleContent.isJoin()) {
                        return ruleContent;
                    }
                    return null;
                }
            } else {
                manjianQueryWrapper.clear();
                manjianQueryWrapper.eq("site_id", param.getSiteId())
                        .eq("status", ShopManjianStatusEnum.ACTIVE.getValue())
                        .eq("goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue())
                        .le("start_time", System.currentTimeMillis() / 1000)
                        .gt("end_time", System.currentTimeMillis() / 1000);
                List<ShopManjian> manjianInfoSelectedGoods = shopManjianMapper.selectList(manjianQueryWrapper);

                if (ObjectUtil.isNotEmpty(manjianInfoSelectedGoods) && ObjectUtil.isNotEmpty(shopManjianGoods) &&
                        shopManjianGoods.get(0).getGoodsType().equals(ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue())) {
                    Optional<ShopManjian> first = shopManjianGoods.stream()
                            .filter(g -> manjianInfoSelectedGoods.stream().anyMatch(o -> o.getManjianId().equals(g.getManjianId())))
                            .map(g -> manjianInfoSelectedGoods.stream().filter(o -> o.getManjianId().equals(g.getManjianId())).findFirst().get())
                            .findFirst();
                    if (first.isPresent() && canJoinManjian(first.get(), param.getSiteId(), param.getMemberId())) {
                        ShopManjianRuleContentVo ruleContent = getRuleContent(first.get(), param.getSiteId(), param.getMemberId());
                        if (ruleContent.isJoin()) {
                            return ruleContent;
                        }

                        return null;
                    }

                }
            }
        }

        return null;
    }

    public ShopManjianRuleContentVo getRuleContent(ShopManjian manjian, Integer siteId, Integer memberId) {
        if (ObjectUtil.isEmpty(manjian)) {
            return null;
        }

        AtomicBoolean isJoin = new AtomicBoolean(false);
        JSONUtil.parseArray(manjian.getRuleJson()).stream().map(JSONUtil::parseObj).forEach(item -> {
            isJoin.set(item.getBool("is_discount") || item.getBool("is_free_shipping") ||
                    item.getBool("is_give_point") || item.getBool("is_give_balance"));

            if (item.getBool("is_give_coupon")) {
                JSONArray coupon = item.getJSONArray("coupon");
                JSONArray array = new JSONArray();
                for (Object o : coupon) {
                    JSONObject jsonObject = JSONUtil.parseObj(o);
                    QueryWrapper<ShopCoupon> couponQueryWrapper = new QueryWrapper<>();
                    couponQueryWrapper.eq("id", jsonObject.getInt("coupon_id"))
                            .eq("status", CouponStatusEnum.NORMAL.getStatus());
                    ShopCoupon shopCoupon = shopCouponMapper.selectOne(couponQueryWrapper);

                    QueryWrapper<ShopCouponMember> couponMemberQueryWrapper = new QueryWrapper<>();
                    couponMemberQueryWrapper.eq("coupon_id", shopCoupon.getId())
                            .eq("site_id", siteId)
                            .eq("member_id", memberId)
                            .ne("status", CouponMemberStatusEnum.INVALID.getStatus());
                    Long couponMemberCount = shopCouponMemberMapper.selectCount(couponMemberQueryWrapper);
                    if (ObjectUtil.isEmpty(shopCoupon)) {
                        continue;
                    }

                    String couponName;
                    if (shopCoupon.getMinConditionMoney().toString().equals("0.00")) {
                        couponName = shopCoupon.getPrice() + "元无门槛券";
                    } else {
                        couponName = "满" + shopCoupon.getMinConditionMoney() + "元减" + shopCoupon.getPrice() + "元券";
                    }

                    jsonObject.set("coupon_name", couponName);
                    if (shopCoupon.getRemainCount() == 0 || couponMemberCount >= shopCoupon.getLimitCount()) {
                        continue;
                    }

                    array.add(jsonObject);
                }

                item.set("coupon", array);
                isJoin.set(!array.isEmpty());
            }

            if (item.getBool("is_give_goods")) {
                JSONArray goods = item.getJSONArray("goods");
                JSONArray array = new JSONArray();
                for (Object good : goods) {
                    JSONObject jsonObject = JSONUtil.parseObj(good);
                    QueryWrapper<ShopGoodsSku> skuQueryWrapper = new QueryWrapper<>();
                    skuQueryWrapper.eq("goods_id", jsonObject.getInt("goods_id"))
                            .eq("sku_id", jsonObject.getInt("sku_id"))
                            .ge("stock", jsonObject.get("num"));
                    ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(skuQueryWrapper);
                    ShopGoods shopGoods = shopGoodsMapper.selectById(sku.getGoodsId());

                    if (ObjectUtil.isEmpty(sku) || shopGoods.getStatus() != 1) {
                        continue;
                    }

                    jsonObject.set("goods_name", shopGoods.getGoodsName())
                            .set("sku_name", sku.getSkuName())
                            .set("sku_image", sku.getSkuImage())
                            .set("price", sku.getPrice());
                    array.add(jsonObject);
                }

                item.set("goods", array);
                isJoin.set(ObjectUtil.isNotEmpty(array));
            }
        });

        ShopManjianRuleContentVo vo = new ShopManjianRuleContentVo();
        BeanUtil.copyProperties(manjian, vo);
        vo.setJoin(isJoin.get());
        return vo;
    }
}
