package com.niu.shop.service.admin.order;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliveryParam;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliverySearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryListVo;

/**
 * 订单批量发货服务接口
 */
public interface IShopOrderBatchDeliveryService {


    /**
     * 订单批量发货列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderBatchDeliveryListVo>
     */
    PageResult<ShopOrderBatchDeliveryListVo> list(PageParam pageParam, ShopOrderBatchDeliverySearchParam searchParam);

    /**
     * 订单批量发货详情
     * @param id 主键ID
     * @return ShopOrderBatchDeliveryInfoVo
     */
    ShopOrderBatchDeliveryInfoVo info(Integer id);

    /**
     * 订单批量发货添加
     * @param addParam 添加参数
     */
    void add(ShopOrderBatchDeliveryParam addParam);

    /**
     * 订单批量发货编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopOrderBatchDeliveryParam editParam);

    /**
     * 订单批量发货删除
     * @param id 主键ID
     */
    void del(Integer id);

}
