package com.niu.shop.service.admin.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.order.ShopOrderDelivery;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.mapper.order.ShopOrderDeliveryMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.admin.order.IShopOrderDeliveryService;
import com.niu.shop.service.admin.order.vo.ShopOrderDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDeliveryListVo;
import com.niu.shop.service.core.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.DeliverySearchParam;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单发货实现
 */
@Service
public class ShopOrderDeliveryServiceImpl implements IShopOrderDeliveryService {
        
    @Resource
    ShopOrderDeliveryMapper shopOrderDeliveryMapper;

    @Resource
    ShopDeliveryCompanyMapper shopDeliveryCompanyMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreOrderDeliveryService coreOrderDeliveryService;

    @Resource
    ICoreOrderService coreOrderService;


    @Override
    public void delivery(OrderDeliveryParam orderDeliveryParam) {
        orderDeliveryParam.setSiteId(RequestUtils.siteId());
        coreOrderDeliveryService.delivery(orderDeliveryParam);
    }

    @Override
    public List<ShopOrderDeliveryListVo> getDeliveryPackageList(Integer orderId) {
        List<ShopOrderDelivery> list = shopOrderDeliveryMapper.selectList(new QueryWrapper<ShopOrderDelivery>().eq("order_id", orderId).eq("site_id", RequestUtils.siteId()));

        List<ShopOrderDeliveryListVo> voList = new LinkedList<>();
        for (ShopOrderDelivery shopOrderDelivery : list) {
            ShopOrderDeliveryListVo vo = new ShopOrderDeliveryListVo();
            BeanUtil.copyProperties(shopOrderDelivery, vo);
            vo.setCompany(shopDeliveryCompanyMapper.selectById(shopOrderDelivery.getExpressCompanyId()));
            List<ShopOrderDeliveryListVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("delivery_id", shopOrderDelivery.getId())), ShopOrderDeliveryListVo.OrderGoods.class);
            vo.setOrderGoods(orderGoods);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public ShopOrderDeliveryInfoVo getDeliveryPackage(Integer id, String mobile) {

        ShopOrderDelivery shopOrderDelivery = shopOrderDeliveryMapper.selectById(id);
        if(ObjectUtil.isEmpty(shopOrderDelivery)) throw new CommonException("物流信息不存在");
        ShopOrderDeliveryInfoVo vo = new ShopOrderDeliveryInfoVo();
        BeanUtil.copyProperties(shopOrderDelivery, vo);
        vo.setCompany(shopDeliveryCompanyMapper.selectById(shopOrderDelivery.getExpressCompanyId()));
        List<ShopOrderDeliveryListVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("delivery_id", shopOrderDelivery.getId())), ShopOrderDeliveryListVo.OrderGoods.class);
        vo.setOrderGoods(orderGoods);
        if(vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()) && !vo.getSubDeliveryType().equals(OrderDeliveryTypeEnum.NONE_EXPRESS.getType())){
            vo.setMobile(mobile);
            //查询物流信息
            DeliverySearchParam deliverySearchParam = new DeliverySearchParam();
            deliverySearchParam.setExpressNo(vo.getCompany().getExpressNo());
            deliverySearchParam.setLogisticNo(vo.getExpressNumber());
            deliverySearchParam.setMobile(mobile);
            vo.setTraces(coreOrderService.deliverySearch(RequestUtils.siteId(), deliverySearchParam));
        }
        return vo;

    }
}
