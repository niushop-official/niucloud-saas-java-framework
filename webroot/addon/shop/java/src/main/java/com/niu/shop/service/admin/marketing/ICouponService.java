package com.niu.shop.service.admin.marketing;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.shop.service.admin.marketing.vo.*;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICouponService {

    /**
     *初始化
     * @return
     */
    JSONObject getInit();

    /**
     * 优惠券列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopCouponListVo>
     */
    PageResult<ShopCouponListVo> getPage(PageParam pageParam, ShopCouponSearchParam searchParam);


    ShopCouponSelectVo getSelectPage(PageParam pageParam, ShopCouponSelectParam searchParam);
    /**
     * 查询指定优惠券
     * @param couponIds
     * @return
     */
    List<ShopCouponListVo> getSelectedList(String couponIds);

    /**
     * 获取优惠券详情
     * @param couponId
     * @return
     */
    ShopCouponInfoVo getInfo(Integer couponId);

    /**
     * 添加优惠券
     * @param addParam
     */
    void add(ShopCouponParam addParam);

    /**
     * 编辑优惠券
     * @param couponId
     * @param shopCouponParam
     */
    void edit(Integer couponId, ShopCouponParam shopCouponParam);

    /**
     * 删除优惠券
     * @param couponId
     */
    void delete(Integer couponId);

    void deleteAll(ShopCouponDelParam shopCouponDelParam);

    /**
     * 获取会员优惠券
     * @param pageParam
     * @param id
     * @param keywords
     * @return
     */
    PageResult<ShopCouponMemberListVo> getMemberCoupon(PageParam pageParam, Integer id, String keywords);

    /**
     * 设置优惠券领用状态
     * @param couponId
     * @param status
     */
    void setStatus(Integer couponId, Integer status);

    /**
     * 设置优惠券失效
     * @param couponId
     */
    void couponInvalid(Integer couponId);

    void couponInvalidAll(ShopCouponDelParam shopCouponDelParam);

    /**
     * 获取发送记录分页列表
     *
     * @param pageParam
     * @param couponId
     * @param param
     * @return
     */
    PageResult<ShopCouponSendRecordsVo> getSendPages(PageParam pageParam, Integer couponId, ShopCouponSendPageParam param);

    /**
     * 发放优惠券
     * @param couponId
     * @param param
     */
    void sendCoupon(Integer couponId, ShopCouponSendParam param);
}
