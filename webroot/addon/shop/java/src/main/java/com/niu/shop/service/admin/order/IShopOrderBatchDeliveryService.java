package com.niu.shop.service.admin.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderAddBatchDeliveryParam;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliverySearchParam;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryListVo;

/**
 * 订单批量发货服务接口
 */
public interface IShopOrderBatchDeliveryService {


    /**
     * 订单批量发货列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderBatchDeliveryListVo>
     */
    PageResult<ShopOrderBatchDeliveryListVo> list(PageParam pageParam, ShopOrderBatchDeliverySearchParam searchParam);

    /**
     * 订单批量发货详情
     *
     * @param id 主键ID
     * @return ShopOrderBatchDeliveryInfoVo
     */
    ShopOrderBatchDeliveryInfoVo info(Integer id);

    void addBatchOrderDelivery(ShopOrderAddBatchDeliveryParam param);

    void execute(Integer id);
}
