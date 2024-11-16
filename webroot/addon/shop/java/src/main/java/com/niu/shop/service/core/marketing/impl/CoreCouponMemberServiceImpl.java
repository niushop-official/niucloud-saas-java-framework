package com.niu.shop.service.core.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreCouponMemberServiceImpl implements ICoreCouponMemberService {

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;

    @Override
    public void use(Integer id, Integer tradeId) {
        ShopCouponMember memberCoupon = shopCouponMemberMapper.selectOne(new QueryWrapper<ShopCouponMember>()
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .eq("id", id));
        if (memberCoupon == null) throw new CommonException("优惠券已使用或已过期");
        if (memberCoupon.getExpireTime().compareTo(System.currentTimeMillis() / 1000) == -1) throw new CommonException("当前优惠券已过期");

        memberCoupon.setTradeId(tradeId);
        memberCoupon.setStatus(CouponMemberStatusEnum.USED.getStatus());
        memberCoupon.setUseTime(System.currentTimeMillis() / 1000);
        shopCouponMemberMapper.updateById(memberCoupon);
    }

    /**
     * 恢复已使用的优惠券
     *
     * @param id
     */
    @Override
    public void recover(Integer id) {
        ShopCouponMember memberCoupon = shopCouponMemberMapper.selectOne(new QueryWrapper<ShopCouponMember>()
                .eq("status", CouponMemberStatusEnum.USED.getStatus())
                .eq("id", id));
        if (memberCoupon != null) {
            memberCoupon.setTradeId(0);
            memberCoupon.setStatus(memberCoupon.getExpireTime().compareTo(System.currentTimeMillis() / 1000) == 1 ? CouponMemberStatusEnum.WAIT_USE.getStatus() : CouponMemberStatusEnum.EXPIRE.getStatus());
            memberCoupon.setUseTime(Long.valueOf(0));
            shopCouponMemberMapper.updateById(memberCoupon);
        }
    }

    /**
     * 优惠券过期
     *
     */
    @Override
    public void expire() {
        ShopCouponMember memberCoupon = new ShopCouponMember();
        memberCoupon.setStatus(CouponMemberStatusEnum.EXPIRE.getStatus());
        shopCouponMemberMapper.update(memberCoupon, new QueryWrapper<ShopCouponMember>()
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .le("expire_time", System.currentTimeMillis() / 1000)
        );
    }
}
