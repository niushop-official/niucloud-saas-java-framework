package com.niu.shop.service.core.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.service.core.marketing.ICoreCouponService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CoreCouponServiceImpl implements ICoreCouponService {

    @Resource
    ShopCouponMapper shopCouponMapper;


    @Override
    public void couponNormal(List<Integer> couponIds) {

        ShopCoupon shopCoupon = new ShopCoupon();
        shopCoupon.setStatus(CouponStatusEnum.NORMAL.getStatus());
        shopCouponMapper.update(shopCoupon, new QueryWrapper<ShopCoupon>()
                .eq("status", CouponStatusEnum.WAIT_START.getStatus())
                .in("id", couponIds));
    }

    @Override
    public void couponExpire(List<Integer> couponIds) {

        ShopCoupon shopCoupon = new ShopCoupon();
        shopCoupon.setStatus(CouponStatusEnum.EXPIRE.getStatus());
        shopCouponMapper.update(shopCoupon, new QueryWrapper<ShopCoupon>()
                .eq("status", CouponStatusEnum.NORMAL.getStatus())
                .in("id", couponIds));
    }
}
