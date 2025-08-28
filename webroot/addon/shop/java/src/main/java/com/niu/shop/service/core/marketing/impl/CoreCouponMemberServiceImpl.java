package com.niu.shop.service.core.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.exception.CommonException;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.coupon.ShopCouponSendRecords;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.mapper.cart.ShopCartMapper;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoreCouponMemberServiceImpl  extends ServiceImpl<ShopCouponMemberMapper, ShopCouponMember> implements ICoreCouponMemberService {

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;

    @Resource
    ShopCouponMapper shopCouponMapper;

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

    @Override
    public void invalid(List<Integer> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            // 如果传入的ids列表为空，直接返回，避免后续不必要的操作
            return;
        }
        QueryWrapper<ShopCouponMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus());
        ShopCouponMember updateMember = new ShopCouponMember();
        updateMember.setStatus(CouponMemberStatusEnum.INVALID.getStatus());
        shopCouponMemberMapper.update(updateMember, queryWrapper);
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

    /**
     * 发放优惠券（这里只进行发放，直接领取数量控制在直接领取方法，累计发放数量）
     *
     * @param siteId  站点ID
     * @param memberId 会员ID
     * @param couponId 优惠券ID
     * @param num 发放数量
     */
    @Override
    public void sendCoupon(Integer siteId, Integer memberId, Integer couponId, Integer num) {
        // 根据优惠券ID和站点ID查询优惠券信息
        QueryWrapper<ShopCoupon> couponQueryWrapper = new QueryWrapper<>();
        couponQueryWrapper.eq("id", couponId).eq("site_id", siteId);
        ShopCoupon coupon = shopCouponMapper.selectOne(couponQueryWrapper);
        if (coupon == null) {
            throw new CommonException("COUPON_NOT_EXIST");
        }

        // 判断领取时间是否符合要求
        long currentTime = System.currentTimeMillis() / 1000;

        long expireTime;
        if (coupon.getValidType() == 1) {
            expireTime = 86400 * coupon.getLength() + currentTime;
        } else {
            expireTime = coupon.getValidEndTime();
        }

        List<ShopCouponMember> memberCouponData = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ShopCouponMember memberCoupon = createShopCouponMember(siteId, couponId, memberId, currentTime, expireTime, coupon);
            memberCouponData.add(memberCoupon);
        }

        this.saveBatch(memberCouponData);

        coupon.setReceiveCount(coupon.getReceiveCount() + num);
        //直接发放不能统计，需要继续优化,防止现有数量为0导致异常，先统计领用数量
/*        if (coupon.getRemainCount()!= -1) {
            coupon.setRemainCount(coupon.getRemainCount() - num);
        }*/
        shopCouponMapper.updateById(coupon);
    }

    /**
     * 批量发送优惠券
     * @param shopCouponSendRecords  发卷信息
     * @param memberIds 会员ID集合
     */
    @Override
    public Integer sendCouponBatch(ShopCouponSendRecords shopCouponSendRecords, List<Integer> memberIds) {
        Integer couponId = shopCouponSendRecords.getCouponId();
        Integer siteId = shopCouponSendRecords.getSiteId();
        Integer num = shopCouponSendRecords.getSendNum();
        // 根据优惠券ID和站点ID查询优惠券信息
        QueryWrapper<ShopCoupon> couponQueryWrapper = new QueryWrapper<>();
        couponQueryWrapper.eq("id", couponId).eq("site_id", siteId);
        ShopCoupon coupon = shopCouponMapper.selectOne(couponQueryWrapper);
        if (coupon == null) {
            throw new CommonException("优惠券不存在");
        }

        // 判断领取时间是否符合要求
        long currentTime = System.currentTimeMillis() / 1000;

        long expireTime = coupon.getValidType() == 1
                ? 86400L * coupon.getLength() + currentTime
                : coupon.getValidEndTime();

        List<ShopCouponMember> memberCouponData = new ArrayList<>();
        memberIds.forEach(memberId -> {
            ShopCouponMember memberCoupon = createShopCouponMember(siteId, couponId, memberId, currentTime, expireTime, coupon);
            for (int i = 0; i < num; i++) {
                memberCouponData.add(memberCoupon);
            }
        });

        this.saveBatch(memberCouponData);
        coupon.setReceiveCount(coupon.getReceiveCount() + memberCouponData.size());
        shopCouponMapper.updateById(coupon);
        return memberCouponData.size();
    }

    public ShopCouponMember createShopCouponMember(Integer siteId, Integer couponId, Integer memberId, Long currentTime, Long expireTime, ShopCoupon coupon) {
        ShopCouponMember memberCoupon = new ShopCouponMember();
        memberCoupon.setSiteId(siteId);
        memberCoupon.setCouponId(couponId);
        memberCoupon.setMemberId(memberId);
        memberCoupon.setCreateTime(currentTime);
        memberCoupon.setExpireTime(expireTime);
        memberCoupon.setReceiveType("send");
        memberCoupon.setType(coupon.getType().toString());
        memberCoupon.setTitle(coupon.getTitle());
        memberCoupon.setPrice(coupon.getPrice());
        memberCoupon.setStatus(CouponMemberStatusEnum.WAIT_USE.getStatus());
        memberCoupon.setMinConditionMoney(coupon.getMinConditionMoney());
        return memberCoupon;
    }



}
