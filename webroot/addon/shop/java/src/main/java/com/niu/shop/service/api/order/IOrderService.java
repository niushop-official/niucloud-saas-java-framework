package com.niu.shop.service.api.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderParam;
import com.niu.shop.service.api.order.param.DeliveryPackageSearchParam;
import com.niu.shop.service.api.order.param.ShopOrderSearchParam;
import com.niu.shop.service.api.order.vo.DeliveryPackageVo;
import com.niu.shop.service.api.order.vo.OrderNumVo;
import com.niu.shop.service.api.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.api.order.vo.ShopOrderListVo;

/**
 * 订单服务接口
 */
public interface IOrderService {


    /**
     * 订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    PageResult<ShopOrderListVo> page(PageParam pageParam, ShopOrderSearchParam searchParam);

    /**
     * 订单详情
     * @param orderId 主键ID
     * @return ShopOrderInfoVo
     */
    ShopOrderInfoVo detail(Integer orderId);

    /**
     * 获取订单数量
     *
     * @return
     */
    OrderNumVo getNum();

    /**
     * 关闭订单
     *
     * @param id
     */
    void close(Integer id);

    /**
     * 订单完成
     *
     * @param id
     */
    void orderFinish(Integer id);


    /**
     * 物流信息
     *
     */
    DeliveryPackageVo getPackage(DeliveryPackageSearchParam searchParam);
}
