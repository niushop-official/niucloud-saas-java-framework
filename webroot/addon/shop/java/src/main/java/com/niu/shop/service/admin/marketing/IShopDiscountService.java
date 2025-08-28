package com.niu.shop.service.admin.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.shop.service.admin.marketing.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 限时折扣服务接口
 */
public interface IShopDiscountService {


    /**
     * 限时折扣列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    PageResult<ShopDiscountListVo> getPage(PageParam pageParam, ShopDiscountSearchParam searchParam);

    /**
     * 限时折扣信息
     *
     * @param id 主键ID
     * @return ShopActiveInfoVo
     */
    ShopDiscountInfoVo info(Integer id);

    /**
     * 限时折扣详情
     *
     * @param id
     * @return
     */
    ShopDiscountDetailVo detail(Integer id);

    void checkGoods(List<ShopDiscountParam.GoodsItemParam> goodsList);

    /**
     * 限时折扣删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 限时折扣添加
     *
     * @param shopDiscountParam
     * @return
     */
    void add(ShopDiscountParam shopDiscountParam);

    /**
     * 限时折扣编辑
     *
     * @param id
     * @param shopDiscountParam
     */
    void edit(Integer id, ShopDiscountParam shopDiscountParam);

    /**
     * 设置商品折扣
     *
     * @param activeId
     * @return
     */
    boolean setGoodsDiscount(Integer activeId);

    /**
     * 取消商品折扣
     *
     * @param activeId
     * @return
     */
    boolean cancelGoodsDiscount(Integer activeId);

    /**
     * 限时折扣关闭
     *
     * @param activeId
     */
    void discountClose(Integer activeId);

    void batchClose(List<Integer> ids);

    void batchDelete(List<Integer> ids);

    /**
     * 限时折扣开始后执行
     *
     * @param activeId
     */
    void discountStartAfter(Integer activeId);

    /**
     * 限时折扣结束后执行
     *
     * @param activeId
     */
    void discountEndAfter(Integer activeId);

    /**
     * 限时折扣订单列表
     *
     * @param activeId
     * @param
     * @return
     */
    PageResult<ShopDiscountOrderListVo> order(Integer activeId, PageParam pageParam, ShopDiscountOrderSearchParam shopDiscountOrderSearchParam);

    /**
     * 限时折扣参与会员列表
     *
     * @param pageParam * @param discountId
     * @return
     */
    PageResult<ShopDiscountMemberListVo> member(PageParam pageParam, Integer discountId, ShopDiscountMemberSearchParam shopDiscountMemberSearchParam);

    /**
     * 限时折扣商品列表
     *
     * @param activeId
     * @param searchParam
     * @return
     */
    PageResult<ShopDiscountGoodsListVo> goods(PageParam pageParam, Integer activeId, ShopDiscountGoodsSearchParam searchParam);

    /**
     * 限时折扣设置
     *
     * @param key
     * @param data
     */
    void setConfig(String key, JSONArray data);

    /**
     * 获取限时折扣设置
     *
     * @return
     */
    JSONArray getDiscountBannerConfig();

    /**
     * 限时折扣订单支付后执行
     *
     * @param order
     */
    void orderPayAfter(ShopOrder order);

    /**
     * 限时折扣商品校验
     *
     * @param body
     */
    Map<String, Object> checkGoodsData(ShopDiscountCheckGoodsParam body);
}
