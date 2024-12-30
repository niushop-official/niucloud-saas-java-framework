package com.niu.shop.service.api.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.entity.order.*;
import com.niu.shop.enums.order.*;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.order.*;
import com.niu.shop.service.api.order.param.DeliveryPackageSearchParam;
import com.niu.shop.service.api.order.param.ShopOrderSearchParam;
import com.niu.shop.service.api.order.vo.DeliveryPackageVo;
import com.niu.shop.service.api.order.vo.OrderNumVo;
import com.niu.shop.service.api.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.api.order.vo.ShopOrderListVo;
import com.niu.shop.service.api.order.IOrderService;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.DeliverySearchParam;
import com.niu.shop.service.core.order.param.OrderCloseParam;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 订单实现
 */
@Service
public class OrderServiceImpl implements IOrderService {
        
    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopOrderDiscountMapper shopOrderDiscountMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    @Resource
    PayMapper payMapper;

    @Resource
    ShopStoreMapper shopStoreMapper;

    @Resource
    ShopOrderDeliveryMapper shopOrderDeliveryMapper;

    @Resource
    ShopOrderRefundMapper shopOrderRefundMapper;

    @Resource
    ICoreOrderService coreOrderService;

    @Resource
    ICoreOrderFinishService coreOrderFinishService;

    @Resource
    ShopDeliveryCompanyMapper shopDeliveryCompanyMapper;

    /**
     * 订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    @Override
    public PageResult<ShopOrderListVo> page(PageParam pageParam, ShopOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("point,activity_type,order_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,create_time,pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,pay_money,is_evaluate");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderNo())) queryWrapper.like("order_no", searchParam.getOrderNo());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getActivityType())) queryWrapper.eq("activity_type", searchParam.getActivityType());
        queryWrapper.orderByDesc("create_time");

        OrderConfigVo orderConfig = coreOrderConfigService.getConfig(RequestUtils.siteId());

        IPage<ShopOrder> iPage = shopOrderMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopOrderListVo> list = new LinkedList<>();
        for (ShopOrder item : iPage.getRecords()) {
            ShopOrderListVo vo = new ShopOrderListVo();
            BeanUtils.copyProperties(item, vo);

            List<ShopOrderListVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", item.getOrderId())), ShopOrderListVo.OrderGoods.class);
            vo.setOrderGoods(orderGoods);

            vo.setExpireTime(vo.getCreateTime() + orderConfig.getCloseOrderInfo().getCloseLength() * 60);

            if (!item.getOutTradeNo().isEmpty()) {
                Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", item.getOutTradeNo()));
                if (pay != null) {
                    ShopOrderListVo.Pay payVo = new ShopOrderListVo.Pay();
                    BeanUtil.copyProperties(pay, payVo);
                    vo.setPay(payVo);
                }
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 订单详情
     *
     * @param orderId 主键
     * @return ShopOrderInfoVo
     */
    @Override
    public ShopOrderInfoVo detail(Integer orderId) {
        ShopOrder model = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                    .eq("order_id", orderId)
                    .eq("site_id", RequestUtils.siteId())
                    .eq("member_id", RequestUtils.memberId())
                    .select("relate_id,activity_type,point,order_id,site_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money,is_evaluate"));

        Assert.notNull(model, "订单不存在");

        ShopOrderInfoVo vo = new ShopOrderInfoVo();
        BeanUtils.copyProperties(model, vo);

        // 查询订单项
        List<ShopOrderInfoVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", vo.getOrderId())), ShopOrderInfoVo.OrderGoods.class);
        vo.setOrderGoods(orderGoods);

        // 查询订单关闭配置
        OrderConfigVo orderConfig = coreOrderConfigService.getConfig(RequestUtils.siteId());
        vo.setExpireTime(vo.getCreateTime() + orderConfig.getCloseOrderInfo().getCloseLength() * 60);

        // 查询订单支付信息
        if (!vo.getOutTradeNo().isEmpty()) {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
            if (pay != null) {
                ShopOrderListVo.Pay payVo = new ShopOrderListVo.Pay();
                BeanUtil.copyProperties(pay, payVo);
                vo.setPay(payVo);
            }
        }

        // 查询订单自提门店
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
            ShopStore store = shopStoreMapper.selectOne(new QueryWrapper<ShopStore>().eq("store_id", vo.getTakeStoreId()));
            if (store != null) {
                ShopOrderInfoVo.Store storeVo = new ShopOrderInfoVo.Store();
                BeanUtil.copyProperties(store, storeVo);
                vo.setStore(storeVo);
            }
        }

        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            List<ShopOrderInfoVo.OrderDelivery> orderDelivery = CollectUtils.convert(shopOrderDeliveryMapper.selectList(new QueryWrapper<ShopOrderDelivery>().eq("order_id", vo.getOrderId())), ShopOrderInfoVo.OrderDelivery.class);
            vo.setOrderDelivery(orderDelivery);
        }

        //查询订单优惠信息
        List<ShopOrderDiscount> shopOrderDiscountList = shopOrderDiscountMapper.selectList(new QueryWrapper<ShopOrderDiscount>().eq("order_id", vo.getOrderId()));

        if(ObjectUtil.isNotEmpty(shopOrderDiscountList))
        {
            for(ShopOrderDiscount shopOrderDiscount : shopOrderDiscountList)
            {
                if(shopOrderDiscount.getDiscountType().equals("coupon"))
                {
                    vo.setCouponMoney(vo.getCouponMoney().add(shopOrderDiscount.getMoney()));
                }
                if(shopOrderDiscount.getDiscountType().equals("manjian"))
                {
                    vo.setManjianDiscountMoney(vo.getManjianDiscountMoney().add(shopOrderDiscount.getMoney()));
                }
            }
            vo.setOrderDiscount(shopOrderDiscountList);
        }

        return vo;
    }

    /**
     * 获取订单数量
     *
     * @return
     */
    @Override
    public OrderNumVo getNum() {
        Integer siteId = RequestUtils.siteId();
        Integer memberId = RequestUtils.memberId();

        OrderNumVo vo = new OrderNumVo();

        vo.setWaitPay(shopOrderMapper.selectCount(new QueryWrapper<ShopOrder>()
                .eq("site_id", siteId)
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.WAIT_PAY.getStatus())
        ));

        vo.setWaitShipping(shopOrderMapper.selectCount(new QueryWrapper<ShopOrder>()
                .eq("site_id", siteId)
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus())
        ));

        vo.setWaitTake(shopOrderMapper.selectCount(new QueryWrapper<ShopOrder>()
                .eq("site_id", siteId)
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.WAIT_TAKE.getStatus())
        ));

        vo.setEvaluate(shopOrderMapper.selectCount(new QueryWrapper<ShopOrder>()
                .eq("site_id", siteId)
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.FINISH.getStatus())
                .eq("is_evaluate", 0)
        ));

        Integer[] refundStatus = new Integer[]{
                OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus(),
                OrderRefundStatusEnum.STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus(),
                OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus(),
                OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus(),
                OrderRefundStatusEnum.STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER.getStatus(),
                OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus(),
                OrderRefundStatusEnum.STORE_REFUND_TRANSFERING.getStatus()
        };
        vo.setRefund(shopOrderRefundMapper.selectCount(new QueryWrapper<ShopOrderRefund>()
                .eq("site_id", siteId)
                .eq("member_id", memberId)
                .in("status", refundStatus)
        ));

        return vo;
    }

    /**
     * 关闭订单
     *
     * @param id
     */
    public void close(Integer id) {
        OrderCloseParam closeParam = new OrderCloseParam();
        closeParam.setOrderId(id);
        closeParam.setSiteId(RequestUtils.siteId());
        closeParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        closeParam.setMainId(RequestUtils.memberId());
        closeParam.setCloseType(OrderCloseTypeEnum.BUYER_CLOSE.getType());
        coreOrderService.close(closeParam);
    }

    /**
     * 订单完成
     *
     * @param id
     */
    @Override
    public void orderFinish(Integer id) {
        ShopOrder order = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .eq("order_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("member_id", RequestUtils.memberId())
                        .select("status"));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_TAKE.getStatus().toString())) throw new CommonException("只有待收货的订单才可以收货");

        OrderFinishParam finishParam = new OrderFinishParam();
        finishParam.setOrderId(id);
        finishParam.setSiteId(RequestUtils.siteId());
        finishParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        finishParam.setMainId(RequestUtils.memberId());
        coreOrderFinishService.finish(finishParam);
    }

    /**
     * 物流信息
     *
     * @param searchParam
     */
    @Override
    public DeliveryPackageVo getPackage(DeliveryPackageSearchParam searchParam) {
        ShopOrderDelivery delivery = shopOrderDeliveryMapper.selectOne(new QueryWrapper<ShopOrderDelivery>()
                .eq("id", searchParam.getId())
                .eq("site_id", RequestUtils.siteId())
        );
        if (delivery == null) return null;

        DeliveryPackageVo vo = new DeliveryPackageVo();
        BeanUtil.copyProperties(delivery, vo);

        DeliveryPackageVo.Company companyVo = new DeliveryPackageVo.Company();
        ShopDeliveryCompany company = shopDeliveryCompanyMapper.selectById(delivery.getExpressCompanyId());
        BeanUtil.copyProperties(company, companyVo);
        vo.setCompany(companyVo);

        List<DeliveryPackageVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("delivery_id", delivery.getId())), DeliveryPackageVo.OrderGoods.class);
        vo.setOrderGoods(orderGoods);

        if(vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()) && !vo.getSubDeliveryType().equals(OrderDeliveryTypeEnum.NONE_EXPRESS.getType())) {
            DeliverySearchParam deliverySearchParam = new DeliverySearchParam();
            deliverySearchParam.setExpressNo(companyVo.getExpressNo());
            deliverySearchParam.setLogisticNo(delivery.getExpressNumber());
            deliverySearchParam.setMobile(searchParam.getMobile());
            vo.setTraces(coreOrderService.deliverySearch(RequestUtils.siteId(), deliverySearchParam));
        }

        return vo;
    }

}
