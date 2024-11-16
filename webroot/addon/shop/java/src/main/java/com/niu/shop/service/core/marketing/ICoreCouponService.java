package com.niu.shop.service.core.marketing;


import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICoreCouponService {

    /**
     * 优惠券启用
     * @param couponIds
     */
    void couponNormal(List<Integer> couponIds);

    /**
     * 优惠券过期
     * @param couponIds
     */
    void couponExpire(List<Integer> couponIds);



}
