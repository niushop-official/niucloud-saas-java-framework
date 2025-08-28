package com.niu.shop.service.api.marketing;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.marketing.param.ShopCouponDelParam;
import com.niu.shop.service.api.marketing.param.ShopCouponParam;
import com.niu.shop.service.api.marketing.param.ShopCouponReceiveParam;
import com.niu.shop.service.api.marketing.param.ShopCouponSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopCouponInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopCouponListVo;
import com.niu.shop.service.api.marketing.vo.ShopCouponMemberListVo;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICouponService {


    /**
     * 优惠券列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopCouponListVo>
     */
    PageResult<ShopCouponListVo> getPage(PageParam pageParam, ShopCouponSearchParam searchParam);

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
    ShopCouponInfoVo getDetail(Integer couponId);

    /**
     * 优惠券领用
     * @param shopCouponReceiveParam
     */
    void receive(ShopCouponReceiveParam shopCouponReceiveParam);

    /**
     * 会员领用优惠券记录
     * @param pageParam
     * @param status
     * @param type
     * @return
     */
    PageResult<ShopCouponMemberListVo> getMemberPage(PageParam pageParam, Integer status, String type);

    /**
     * 获取不同状态下优惠券记录
     * @return
     */
    List<JSONObject> getCouponCountByStatus();

    /**
     * 会员已领取数量
     * @return
     */
    Long getMemberCount();

    /**
     * 会员已领取数量
     * @return
     */
    Long getMemberCount(Integer status);

    /**
     * 获取领取方式
     * @return
     */
    List<String> getReceiveType();

    /**
     * 组装数据获取优惠券类型
     * @return
     */
    JSONArray getCouponType();

    /**
     * 组件获取优惠券
     * @param couponIds
     * @param num
     * @return
     */
    List<ShopCouponListVo> getCouponComponents(Integer[] couponIds, Integer num);

    /**
     * 获取优惠券二维码
     * @param couponId
     * @return
     */
    String getQrcode(Integer couponId);

    void invalidCoupon(ShopCouponDelParam shopCouponDelParam);

}
