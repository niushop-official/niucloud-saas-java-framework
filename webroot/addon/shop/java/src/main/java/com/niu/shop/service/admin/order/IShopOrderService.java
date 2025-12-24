package com.niu.shop.service.admin.order;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.*;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.order.vo.EditDeliveryVo;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderListVo;

/**
 * 订单服务接口
 */
public interface IShopOrderService {


    /**
     * 订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    PageResult<ShopOrderListVo> page(PageParam pageParam, ShopOrderSearchParam searchParam);

    /**
     * 订单详情
     * @param id 主键ID
     * @return ShopOrderInfoVo
     */
    ShopOrderInfoVo detail(Integer id);

    /**
     * 关闭订单
     *
     * @param id
     */
    void close(Integer id);

    /**
     * 设置商家备注
     *
     * @param param
     */
    void setShopRemark(SetShopRemarkParam param);

    /**
     * 订单调价
     *
     * @param param
     */
    void editPrice(EditPriceParam param);

    /**
     * 获取订单配送修改信息
     *
     * @param param
     * @return
     */
    EditDeliveryVo getEditDeliveryData(EditDeliveryParam param);

    /**
     * 修改订单配送
     *
     * @param param
     */
    void editDelivery(EditDeliveryParam param);

     /**
      * 删除订单
      *
      * @param param
      */
    void delete(DeleteOrderParam param);
}
