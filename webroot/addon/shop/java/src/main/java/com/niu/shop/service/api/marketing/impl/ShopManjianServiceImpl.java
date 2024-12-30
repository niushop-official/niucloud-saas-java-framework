package com.niu.shop.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.manjian.ShopManjianGoods;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
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
import com.niu.shop.service.api.marketing.vo.ShopManjianContentVo;
import com.niu.shop.service.api.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.marketing.model.ShopManjianCoupon;
import com.niu.shop.service.core.marketing.model.ShopManjianRule;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ShopManjianInfoVo getManjianInfo(ShopManjianInfoParam param) {

        Integer goodsId = param.getGoodsId();
        Integer skuId = param.getSkuId();
        if (ObjectUtil.isEmpty(skuId) && ObjectUtil.isNotEmpty(goodsId)) {

            ShopGoodsSku defaultSkuInfo = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                    .eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId()).eq("is_default", 1).last("limit 1"));
            if(ObjectUtil.isNotEmpty(defaultSkuInfo))
            {
                skuId = defaultSkuInfo.getSkuId();
            }
        }
        if(ObjectUtil.isNotEmpty(skuId) && ObjectUtil.isEmpty(goodsId))
        {
            ShopGoodsSku defaultSkuInfo = shopGoodsSkuMapper.selectById(skuId);
            if(ObjectUtil.isNotEmpty(defaultSkuInfo))
            {
                goodsId = defaultSkuInfo.getGoodsId();
            }
        }
        ShopManjianInfoVo manjianInfo = new ShopManjianInfoVo();

        ShopManjian manjianInfoAllGoods = shopManjianMapper.selectOne(new QueryWrapper<ShopManjian>()
                    .eq("site_id", RequestUtils.siteId()).eq("status", ShopManjianStatusEnum.ACTIVE.getValue()).eq("goods_type", ShopManjianGoodsTypeEnum.ALL_GOODS.getValue()));


        if (ObjectUtil.isNotEmpty(manjianInfoAllGoods)) {
            // 全部商品参与
            BeanUtils.copyProperties(manjianInfoAllGoods, manjianInfo);
            if(ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            {
                boolean canJoin = coreManjianService.canJoinManjian(manjianInfoAllGoods, RequestUtils.siteId(), RequestUtils.memberId());
                if (canJoin) {
                    manjianInfo = getRuleContent(manjianInfo);
                }
            }

        } else {
            ShopManjian manjianInfoSelectedGoodsNot = shopManjianMapper.selectOne(new QueryWrapper<ShopManjian>()
                    .eq("site_id", RequestUtils.siteId()).eq("status", ShopManjianStatusEnum.ACTIVE.getValue()).eq("goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue()).last("limit 1"));

            ShopManjianGoods manjianGoodsInfo = shopManjianGoodsMapper.selectOne(new QueryWrapper<ShopManjianGoods>()
                    .eq("site_id", RequestUtils.siteId()).eq("goods_id", goodsId).eq("sku_id", skuId).eq("status", ShopManjianStatusEnum.ACTIVE.getValue()).last("limit 1"));

            if (ObjectUtil.isNotEmpty(manjianInfoSelectedGoodsNot) && ObjectUtil.isEmpty(manjianGoodsInfo)) {
                // 指定商品不参与
                BeanUtils.copyProperties(manjianInfoSelectedGoodsNot, manjianInfo);
                if(ObjectUtil.isNotEmpty(RequestUtils.memberId()))
                {
                    boolean canJoin = coreManjianService.canJoinManjian(manjianInfoSelectedGoodsNot, RequestUtils.siteId(), RequestUtils.memberId());
                    if (canJoin) {
                        manjianInfo = getRuleContent(manjianInfo);
                    }
                }

            } else {
                // 指定商品参与
                List<ShopManjian> manjianInfoSelectedGoods = shopManjianMapper.selectList(new QueryWrapper<ShopManjian>()
                        .eq("site_id", RequestUtils.siteId()).eq("status", ShopManjianStatusEnum.ACTIVE.getValue()).eq("goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue()));

                if (ObjectUtil.isNotEmpty(manjianInfoSelectedGoods) && ObjectUtil.isNotEmpty(manjianGoodsInfo) &&manjianGoodsInfo.getGoodsType().equals(ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue())) {
                    ShopManjian selectedManjianInfo = manjianInfoSelectedGoods.stream().filter(m -> m.getManjianId().equals(manjianGoodsInfo.getManjianId())).findFirst().orElse(null);
                    if (ObjectUtil.isNotEmpty(selectedManjianInfo))  {
                        BeanUtils.copyProperties(selectedManjianInfo, manjianInfo);
                        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
                            boolean canJoin = coreManjianService.canJoinManjian(selectedManjianInfo, RequestUtils.siteId(), RequestUtils.memberId());
                            if (canJoin) {
                                manjianInfo = getRuleContent(manjianInfo);
                            }
                        }
                    }

                }
            }
        }
        if(ObjectUtil.isEmpty(manjianInfo.getRuleJson())) return null;
        return manjianInfo;
    }

    @Override
    public ShopManjianInfoVo getRuleContent(ShopManjianInfoVo vo) {
        if (vo!= null && vo.getRuleJson()!= null &&!vo.getRuleJson().isEmpty()) {
            boolean isJoin = false;
            List<ShopManjianRule> ruleJsonList = JSONUtil.toList(vo.getRuleJson(), ShopManjianRule.class);
            for (int i = 0; i < ruleJsonList.size(); i++) {
                ShopManjianRule item = ruleJsonList.get(i);
                if (item.getIsDiscount()) {
                    isJoin = true;
                }
                if (item.getIsFreeShipping()) {
                    isJoin = true;
                }
                if (item.getIsGivePoint()) {
                    isJoin = true;
                }
                if (item.getIsGiveBalance()) {
                    isJoin = true;
                }
                if (item.getIsGiveCoupon()) {
                    List<ShopManjianCoupon> couponList = item.getCoupon();
                    if (couponList!= null &&!couponList.isEmpty()) {
                        for (int j = 0; j < couponList.size(); j++) {
                            ShopManjianCoupon coupon = couponList.get(j);

                            ShopCoupon couponEntity = shopCouponMapper.selectOne(new QueryWrapper<ShopCoupon>()
                                    .eq("id", coupon.getCouponId())
                                    .eq("status", CouponStatusEnum.NORMAL.getStatus()));
                            Long memberCouponCount = shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>()
                                    .eq("coupon_id", coupon.getCouponId())
                                    .eq("site_id", RequestUtils.siteId())
                                    .eq("member_id", RequestUtils.memberId())
                                    .ne("status", CouponMemberStatusEnum.INVALID.getStatus()));
                            if (ObjectUtil.isNotEmpty(couponEntity)) {
                                String couponName;
                                if (couponEntity.getMinConditionMoney().compareTo(BigDecimal.ZERO) == 0) {
                                    couponName = couponEntity.getPrice() + "元无门槛券";
                                } else {
                                    couponName = "满" + couponEntity.getMinConditionMoney() + "元减" + couponEntity.getPrice() + "元券";
                                }
                                coupon.setCouponName(couponName);
                                if (couponEntity.getRemainCount() == 0 || memberCouponCount >= couponEntity.getLimitCount()) {
                                    couponList.remove(j);
                                    j--;
                                }
                            } else {
                                couponList.remove(j);
                                j--;
                            }
                        }
                        item.setCoupon(couponList);
                        if (!couponList.isEmpty()) {
                            isJoin = true;
                        }
                    }
                }
                if (item.getIsGiveGoods()) {
                    List<ShopManjianRule.GoodsDTO> goodsList = item.getGoods();
                    if (ObjectUtil.isNotEmpty(goodsList)) {
                        for (int k = 0; k < goodsList.size(); k++) {
                            ShopManjianRule.GoodsDTO goods = goodsList.get(k);

                            ShopGoodsSku skuInfo = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                                    .eq("goods_id", goods.getGoodsId())
                                    .eq("sku_id", goods.getSkuId())
                                    .ge("stock", goods.getNum()));
                            ShopGoods goodsInfo = shopGoodsMapper.selectById(goods.getGoodsId());
                            if (ObjectUtil.isNotEmpty(skuInfo)) {
                                goods.setGoodsName(goodsInfo.getGoodsName());
                                goods.setSkuName(skuInfo.getSkuName());
                                goods.setSkuImage(skuInfo.getSkuImage());
                                goods.setPrice(skuInfo.getPrice());
                            } else {
                                goodsList.remove(k);
                                k--;
                            }
                        }
                        item.setGoods(goodsList);
                        if (!goodsList.isEmpty()) {
                            isJoin = true;
                        }
                    }
                }
                ruleJsonList.set(i, item);
            }
            vo.setIsJoin(isJoin);
        }
        return vo;
    }
}
