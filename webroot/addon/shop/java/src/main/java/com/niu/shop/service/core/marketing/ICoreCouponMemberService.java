package com.niu.shop.service.core.marketing;

public interface ICoreCouponMemberService {

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
     * 优惠券过期
     */
    void expire();
}
