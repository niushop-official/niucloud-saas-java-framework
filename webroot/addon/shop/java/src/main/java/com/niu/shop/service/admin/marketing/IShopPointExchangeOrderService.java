package com.niu.shop.service.admin.marketing;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeOrderParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeOrderSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeOrderInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeOrderListVo;

/**
 * 积分兑换订单服务接口
 */
public interface IShopPointExchangeOrderService {


    /**
     * 积分兑换订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopPointExchangeOrderListVo>
     */
    PageResult<ShopPointExchangeOrderListVo> list(PageParam pageParam, ShopPointExchangeOrderSearchParam searchParam);

    /**
     * 积分兑换订单详情
     * @param id 主键ID
     * @return ShopPointExchangeOrderInfoVo
     */
    ShopPointExchangeOrderInfoVo info(Integer id);

    /**
     * 积分兑换订单添加
     * @param addParam 添加参数
     */
    void add(ShopPointExchangeOrderParam addParam);

    /**
     * 积分兑换订单编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopPointExchangeOrderParam editParam);

    /**
     * 积分兑换订单删除
     * @param id 主键ID
     */
    void del(Integer id);

}
