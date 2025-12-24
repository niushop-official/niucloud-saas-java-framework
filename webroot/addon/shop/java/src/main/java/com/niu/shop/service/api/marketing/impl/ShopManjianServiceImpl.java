package com.niu.shop.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.manjian.ShopManjianGoods;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.enums.marketing.ShopManjianGoodsTypeEnum;
import com.niu.shop.enums.marketing.ShopManjianStatusEnum;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.manjian.ShopManjianGoodsMapper;
import com.niu.shop.mapper.manjian.ShopManjianMapper;
import com.niu.shop.service.api.marketing.IShopManjianService;
import com.niu.shop.service.api.marketing.param.ShopManjianInfoParam;
import com.niu.shop.service.api.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.marketing.model.ShopManjianCoupon;
import com.niu.shop.service.core.marketing.model.ShopManjianRule;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ShopManjianServiceImpl implements IShopManjianService {

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    ShopManjianMapper shopManjianMapper;

    @Resource
    ShopManjianGoodsMapper shopManjianGoodsMapper;

    @Resource
    ICoreManjianService coreManjianService;

    @Resource
    ShopCouponMapper shopCouponMapper;

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Override
    public List<ShopManjianInfoVo> getManjianInfo(ShopManjianInfoParam param) {
        Integer goodsId = param.getGoodsId();
        Integer skuId = param.getSkuId();
        List<ShopManjianRule.GoodsDTO> giftGoods = param.getGiftGoods();
        Integer memberId = RequestUtils.memberId();
        Integer siteId = RequestUtils.siteId();
        long currTime = DateUtils.currTime();

        if (ObjectUtil.isEmpty(skuId) && ObjectUtil.isEmpty(goodsId)) {
            return List.of();
        }

        if (ObjectUtil.isEmpty(skuId) && ObjectUtil.isNotEmpty(goodsId)) {
            ShopGoodsSku defaultSkuInfo = shopGoodsSkuMapper.selectOne(new LambdaQueryWrapper<ShopGoodsSku>()
                    .eq(ShopGoodsSku::getGoodsId, goodsId).eq(ShopGoodsSku::getSiteId, siteId)
                    .eq(ShopGoodsSku::getIsDefault, 1).last("limit 1"));
            skuId = ObjectUtil.isNotEmpty(defaultSkuInfo) ? defaultSkuInfo.getSkuId() : null;
        }

        List<ShopManjianInfoVo> allManjianInfo = new ArrayList<>();

        LambdaQueryWrapper<ShopManjian> baseQuery = new LambdaQueryWrapper<ShopManjian>()
                .eq(ShopManjian::getSiteId, siteId)
                .eq(ShopManjian::getStatus, ShopManjianStatusEnum.ACTIVE.getValue())
                .le(ShopManjian::getStartTime, currTime)
                .gt(ShopManjian::getEndTime, currTime);

        ShopManjian allGoodsManjian = shopManjianMapper.selectOne(
                baseQuery.eq(ShopManjian::getGoodsType, ShopManjianGoodsTypeEnum.ALL_GOODS.getValue())
                        .last("limit 1"));
        if (ObjectUtil.isNotEmpty(allGoodsManjian)) {
            addManjianInfo(allManjianInfo, allGoodsManjian, siteId, memberId, giftGoods);
        }

        List<ShopManjian> selectedGoodsNotList = shopManjianMapper.selectList(baseQuery
                .eq(ShopManjian::getGoodsType, ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue()));
        if (CollectionUtils.isNotEmpty(selectedGoodsNotList)) {
            selectedGoodsNotList.forEach(manjian -> addManjianInfo(allManjianInfo, manjian, siteId, memberId, giftGoods));
        }

        List<ShopManjian> selectedGoodsList = shopManjianMapper.selectList(
                baseQuery.eq(ShopManjian::getGoodsType, ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue()));
        if (CollectionUtils.isNotEmpty(selectedGoodsList)) {
            ShopManjianGoods manjianGoodsInfo = shopManjianGoodsMapper.selectOne(
                    new LambdaQueryWrapper<ShopManjianGoods>().select(ShopManjianGoods::getManjianId)
                            .eq(ShopManjianGoods::getSiteId, siteId).eq(ShopManjianGoods::getGoodsId, goodsId)
                            .eq(ShopManjianGoods::getSkuId, skuId).eq(ShopManjianGoods::getStatus, ShopManjianStatusEnum.ACTIVE.getValue())
                            .last("limit 1"));

            if (ObjectUtil.isNotEmpty(manjianGoodsInfo)) {
                selectedGoodsList.stream()
                        .filter(manjian -> manjian.getManjianId().equals(manjianGoodsInfo.getManjianId()))
                        .findFirst()
                        .ifPresent(manjian ->
                                addManjianInfo(allManjianInfo, manjian, siteId, memberId, giftGoods));
            }
        }

        return allManjianInfo;
    }

    /**
     * 添加满减活动信息到结果列表
     */
    private void addManjianInfo(List<ShopManjianInfoVo> resultList, ShopManjian manjian, Integer siteId, Integer memberId, List<ShopManjianRule.GoodsDTO> giftGoods) {
        ShopManjianInfoVo manjianInfoVo = new ShopManjianInfoVo();
        BeanUtils.copyProperties(manjian, manjianInfoVo);

        boolean canJoin = coreManjianService.canJoinManjian(manjian, siteId, memberId);
        if (canJoin) {
            ShopManjianInfoVo ruleContent = getRuleContent(manjianInfoVo, giftGoods);
            if (ruleContent.getIsJoin()) {
                manjianInfoVo = ruleContent;
            }
        }
        resultList.add(manjianInfoVo);
    }

    @Override
    public ShopManjianInfoVo getRuleContent(ShopManjianInfoVo vo, List<ShopManjianRule.GoodsDTO> giftGoods) {
        if (vo != null && vo.getRuleJson() != null && !vo.getRuleJson().isEmpty()) {
            boolean isJoin = false;
            vo.setGiftGoods(giftGoods);
            List<ShopManjianRule> ruleJsonList = JSONUtil.toList(vo.getRuleJson(), ShopManjianRule.class);
            for (int i = 0; i < ruleJsonList.size(); i++) {
                ShopManjianRule item = ruleJsonList.get(i);
                if (item.getIsDiscount() || item.getIsFreeShipping() ||
                        item.getIsGivePoint() || item.getIsGiveBalance()) {
                    isJoin = true;
                }
                if (item.getIsGiveCoupon() && !CollectionUtils.isEmpty(item.getCoupon())) {
                    List<ShopManjianCoupon> couponList = item.getCoupon();
                    List<ShopManjianCoupon> validCoupons = new ArrayList<>();
                    for (ShopManjianCoupon coupon : couponList) {
                        ShopCoupon couponEntity = shopCouponMapper.selectOne(new LambdaQueryWrapper<ShopCoupon>()
                                .select(ShopCoupon::getPrice, ShopCoupon::getMinConditionMoney)
                                .eq(ShopCoupon::getId, coupon.getCouponId())
                                .eq(ShopCoupon::getStatus, CouponStatusEnum.NORMAL.getStatus()));

                        if (ObjectUtil.isNotEmpty(couponEntity)) {
                            String couponName;
                            if (couponEntity.getMinConditionMoney().compareTo(BigDecimal.ZERO) == 0) {
                                couponName = couponEntity.getPrice() + "元无门槛券";
                            } else {
                                couponName = "满" + couponEntity.getMinConditionMoney() + "元减" + couponEntity.getPrice() + "元券";
                            }
                            coupon.setCouponName(couponName);
                            validCoupons.add(coupon);
                        }
                    }

                    item.setCoupon(validCoupons);
                    if (!validCoupons.isEmpty()) {
                        isJoin = true;
                    }
                }
                if (item.getIsGiveGoods() && !CollectionUtils.isEmpty(item.getGoods())) {
                    List<ShopManjianRule.GoodsDTO> goodsList = item.getGoods();
                    List<ShopManjianRule.GoodsDTO> validGoods = new ArrayList<>();
                    if (ObjectUtil.isNotEmpty(goodsList)) {
                        for (ShopManjianRule.GoodsDTO goods : goodsList) {
                            ShopGoodsSku skuInfo = shopGoodsSkuMapper.selectOne(new LambdaQueryWrapper<ShopGoodsSku>()
                                    .select(ShopGoodsSku::getGoodsId, ShopGoodsSku::getSkuName, ShopGoodsSku::getSkuImage, ShopGoodsSku::getPrice, ShopGoodsSku::getStock)
                                    .eq(ShopGoodsSku::getGoodsId, goods.getGoodsId()).eq(ShopGoodsSku::getSkuId, goods.getSkuId()).gt(ShopGoodsSku::getStock, 0));
                            ShopGoods goodsInfo = shopGoodsMapper.selectById(goods.getGoodsId());
                            if (ObjectUtil.isNotEmpty(skuInfo) && ObjectUtil.isNotEmpty(goodsInfo) && goodsInfo.getStatus() == 1) {
                                int availableNum = calculateAvailableNum(goods, skuInfo, giftGoods);
                                if (availableNum > 0){
                                    goods.setGoodsName(goodsInfo.getGoodsName());
                                    goods.setSkuName(skuInfo.getSkuName());
                                    goods.setSkuImage(skuInfo.getSkuImage());
                                    goods.setPrice(skuInfo.getPrice());
                                    if (StringUtils.isEmpty(goods.getSkuImage())) {
                                        goods.setSkuImage(CommonUtils.thumbImageMiddle(goodsInfo.getSiteId(), goodsInfo.getGoodsCover()));
                                    }
                                    validGoods.add(goods);
                                }
                            }
                        }
                        item.setGoods(validGoods);
                        if (!validGoods.isEmpty()) {
                            isJoin = true;
                        }
                    }
                }
                ruleJsonList.set(i, item);
            }
            vo.setRuleJson(JSONUtil.toJsonStr(ruleJsonList));
            vo.setIsJoin(isJoin);
        }
        return vo;
    }

    private int calculateAvailableNum(ShopManjianRule.GoodsDTO goods, ShopGoodsSku skuInfo,
                                      List<ShopManjianRule.GoodsDTO> giftGoods) {
        int stock = skuInfo.getStock();

        if (CollectionUtils.isEmpty(giftGoods)) {
            return Math.min(goods.getNum(), stock);
        }

        int usedStock = giftGoods.stream()
                .filter(gift -> Objects.equals(gift.getGoodsId(), goods.getGoodsId()) &&
                        Objects.equals(gift.getSkuId(), goods.getSkuId()))
                .mapToInt(ShopManjianRule.GoodsDTO::getNum)
                .sum();

        int availableStock = Math.max(0, stock - usedStock);
        return Math.min(goods.getNum(), availableStock);
    }
}
