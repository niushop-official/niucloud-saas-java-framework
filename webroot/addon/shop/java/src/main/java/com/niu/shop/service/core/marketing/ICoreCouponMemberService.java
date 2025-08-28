package com.niu.shop.service.core.marketing;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.coupon.ShopCouponSendRecords;

import java.util.List;

public interface ICoreCouponMemberService  extends IService<ShopCouponMember> {

    /**
     * 使用优惠券
     *
     * @param id
     * @param tradeId
     */
    void use(Integer id, Integer tradeId);

    /**
     * 恢复已使用的优惠券
     *
     * @param id
     */
    void recover(Integer id);

    /**
     * 优惠券失效
     * @param ids  优惠券ids
     */
    void invalid(List<Integer> ids);

    /**
     * 优惠券过期
     */
    void expire();

    /**
     * 发放优惠券
     *
     * @param siteId  站点ID
     * @param memberId 会员ID
     * @param couponId 优惠券ID
     * @param num 发放数量
     */
    void sendCoupon(Integer siteId, Integer memberId, Integer couponId, Integer num);


    /**
     * 批量发放优惠券
     * @param shopCouponSendRecords
     * @param memberIds
     */
    Integer sendCouponBatch(ShopCouponSendRecords shopCouponSendRecords, List<Integer> memberIds);
}
