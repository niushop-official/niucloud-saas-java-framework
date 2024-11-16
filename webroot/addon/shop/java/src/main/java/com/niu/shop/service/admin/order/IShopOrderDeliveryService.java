package com.niu.shop.service.admin.order;

import com.niu.shop.service.admin.order.vo.ShopOrderDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDeliveryListVo;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;

import java.util.List;

/**
 * 订单发货服务接口
 */
public interface IShopOrderDeliveryService {

    void delivery(OrderDeliveryParam orderDeliveryParam);

    List<ShopOrderDeliveryListVo> getDeliveryPackageList(Integer orderId);

    ShopOrderDeliveryInfoVo getDeliveryPackage(Integer id, String mobile);



}
