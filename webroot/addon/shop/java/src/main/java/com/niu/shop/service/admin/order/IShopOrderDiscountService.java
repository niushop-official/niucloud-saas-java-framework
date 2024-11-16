package com.niu.shop.service.admin.order;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountParam;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountListVo;

/**
 * 订单优惠服务接口
 */
public interface IShopOrderDiscountService {


    /**
     * 订单优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountListVo>
     */
    PageResult<ShopOrderDiscountListVo> list(PageParam pageParam, ShopOrderDiscountSearchParam searchParam);

    /**
     * 订单优惠详情
     * @param id 主键ID
     * @return ShopOrderDiscountInfoVo
     */
    ShopOrderDiscountInfoVo info(Integer id);

    /**
     * 订单优惠添加
     * @param addParam 添加参数
     */
    void add(ShopOrderDiscountParam addParam);

    /**
     * 订单优惠编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopOrderDiscountParam editParam);

    /**
     * 订单优惠删除
     * @param id 主键ID
     */
    void del(Integer id);

}
