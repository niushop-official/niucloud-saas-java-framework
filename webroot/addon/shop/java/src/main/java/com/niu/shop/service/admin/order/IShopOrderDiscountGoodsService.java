package com.niu.shop.service.admin.order;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountGoodsParam;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountGoodsSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountGoodsInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountGoodsListVo;

/**
 * 订单项优惠服务接口
 */
public interface IShopOrderDiscountGoodsService {


    /**
     * 订单项优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountGoodsListVo>
     */
    PageResult<ShopOrderDiscountGoodsListVo> list(PageParam pageParam, ShopOrderDiscountGoodsSearchParam searchParam);

    /**
     * 订单项优惠详情
     * @param id 主键ID
     * @return ShopOrderDiscountGoodsInfoVo
     */
    ShopOrderDiscountGoodsInfoVo info(Integer id);

    /**
     * 订单项优惠添加
     * @param addParam 添加参数
     */
    void add(ShopOrderDiscountGoodsParam addParam);

    /**
     * 订单项优惠编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopOrderDiscountGoodsParam editParam);

    /**
     * 订单项优惠删除
     * @param id 主键ID
     */
    void del(Integer id);

}
