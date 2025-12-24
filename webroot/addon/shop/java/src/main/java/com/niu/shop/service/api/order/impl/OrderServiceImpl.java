package com.niu.shop.service.api.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.*;
import com.niu.shop.enums.order.*;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.*;
import com.niu.shop.service.api.order.param.DeliveryPackageSearchParam;
import com.niu.shop.service.api.order.param.ShopOrderSearchParam;
import com.niu.shop.service.api.order.vo.*;
import com.niu.shop.service.api.order.IOrderService;
import com.niu.shop.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.DeliverySearchParam;
import com.niu.shop.service.core.order.param.OrderCloseParam;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 订单实现
 */
@Log4j2
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

    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Resource
    private ShopInvoiceMapper shopInvoiceMapper;

    @Resource
    private ICoreWeappDeliveryService coreWeappDeliveryService;

    @Resource
    private ICorePayChannelService corePayChannelService;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;

    @Resource
    private ICoreManjianService coreManjianService;

    /**
     * 订单列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    @Override
    public ShopOrderPageVo page(PageParam pageParam, ShopOrderSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        Integer siteId = RequestUtils.siteId();
        Integer memberId = RequestUtils.memberId();
        ShopOrderPageVo result = new ShopOrderPageVo();
        result.setCurrentPage(page);
        result.setPerPage(limit);
        result.setTotal(0);
        result.setData(List.of());

        QueryWrapper<ShopOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("point,activity_type,order_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,create_time,pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,pay_money,is_evaluate,invoice_id,site_id");
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("member_id", memberId);
        if (ObjectUtil.isNotEmpty(searchParam.getOrderNo())) queryWrapper.like("order_no", searchParam.getOrderNo());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getActivityType()))
            queryWrapper.eq("activity_type", searchParam.getActivityType());
        if (ObjectUtil.isNotEmpty(searchParam.getBody())) queryWrapper.like("body", searchParam.getBody());
        queryWrapper.orderByDesc("create_time");

        OrderConfigVo orderConfig = coreOrderConfigService.getConfig(siteId);

        IPage<ShopOrder> iPage = shopOrderMapper.selectPage(new Page<>(page, limit), queryWrapper);
        if (CollectionUtils.isEmpty(iPage.getRecords())) {
            return result;
        }
        List<ShopOrder> orderList = iPage.getRecords();
        Set<Integer> orderIds = orderList.stream().map(ShopOrder::getOrderId).collect(Collectors.toSet());

        //批量查询订单商品
        Map<Integer, List<ShopOrderGoods>> goodsByOrderId = shopOrderGoodsMapper.selectList(new LambdaQueryWrapper<ShopOrderGoods>()
                .in(ShopOrderGoods::getOrderId, orderIds)).stream().collect(Collectors.groupingBy(ShopOrderGoods::getOrderId));

        Set<Integer> deliveryIds = goodsByOrderId.values().stream().flatMap(List::stream).map(ShopOrderGoods::getDeliveryId)
                .filter(Objects::nonNull).filter(deliveryId -> deliveryId > 0).collect(Collectors.toSet());

        //单位map
        Set<Integer> goodsIds = goodsByOrderId.values().stream().flatMap(List::stream).map(ShopOrderGoods::getGoodsId).collect(Collectors.toSet());
        Map<Integer, String> unitMap = !CollectionUtils.isEmpty(goodsIds) ?
                shopGoodsMapper.selectByIds(goodsIds)
                        .stream().collect(Collectors.toMap(ShopGoods::getGoodsId, ShopGoods::getUnit)) : Map.of();


        //批量查询物流信息
        Map<Integer, ShopOrderDelivery> deliveryMap = !CollectionUtils.isEmpty(deliveryIds) ?
                shopOrderDeliveryMapper.selectByIds(deliveryIds)
                        .stream().collect(Collectors.toMap(ShopOrderDelivery::getId, Function.identity())) :
                Map.of();

        Set<Integer> companyIds = deliveryMap.values().stream()
                .map(ShopOrderDelivery::getExpressCompanyId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        //批量查询快递公司信息
        Map<Integer, ShopDeliveryCompany> companyMap = !CollectionUtils.isEmpty(companyIds) ?
                shopDeliveryCompanyMapper.selectByIds(companyIds)
                        .stream().collect(Collectors.toMap(ShopDeliveryCompany::getCompanyId, Function.identity())) : Map.of();

        //门店数据
        Set<String> storeIds = orderList.stream().map(ShopOrder::getTakerStoreId).collect(Collectors.toSet());
        Map<Integer, ShopStore> storeMap = !CollectionUtils.isEmpty(storeIds) ?
                shopStoreMapper.selectByIds(storeIds)
                        .stream().collect(Collectors.toMap(ShopStore::getStoreId, Function.identity())) : Map.of();

        //支付数据
        Set<String> outTradeNos = orderList.stream().map(ShopOrder::getOutTradeNo).collect(Collectors.toSet());
        Map<String, Pay> payMap = !CollectionUtils.isEmpty(outTradeNos) ?
                payMapper.selectList(new LambdaQueryWrapper<Pay>().in(Pay::getOutTradeNo, outTradeNos))
                        .stream().collect(Collectors.toMap(Pay::getOutTradeNo, Function.identity())) : Map.of();

        //发票数据
        Set<Integer> invoiceIds = orderList.stream().filter(item -> item.getInvoiceId() > 0).map(ShopOrder::getInvoiceId).collect(Collectors.toSet());
        Set<Integer> invoiceIdSet = !CollectionUtils.isEmpty(invoiceIds) ?
                shopInvoiceMapper.selectList(new LambdaQueryWrapper<ShopInvoice>().select(ShopInvoice::getId).in(ShopInvoice::getId, invoiceIds))
                        .stream().map(ShopInvoice::getId).collect(Collectors.toSet()) : new HashSet<Integer>();
        List<ShopOrderListVo> list = new LinkedList<>();
        for (ShopOrder item : orderList) {
            ShopOrderListVo vo = new ShopOrderListVo();
            BeanUtils.copyProperties(item, vo);

            List<ShopOrderGoods> orderGoodsList = goodsByOrderId.get(item.getOrderId());
            if (!CollectionUtils.isEmpty(orderGoodsList)) {
                List<ShopOrderListVo.OrderGoods> goodsVoList = orderGoodsList.stream().map(goods -> {
                    ShopOrderListVo.OrderGoods goodsVo = new ShopOrderListVo.OrderGoods();
                    BeanUtils.copyProperties(goods, goodsVo);
                    if (unitMap.containsKey(goods.getGoodsId())) {
                        goodsVo.setUnit(unitMap.get(goods.getGoodsId()));
                    }
                    if (ObjectUtils.isNotEmpty(goods.getDeliveryId())) {
                        ShopOrderDelivery delivery = deliveryMap.get(goods.getDeliveryId());
                        if (ObjectUtils.isNotEmpty(delivery)) {
                            ShopInvoiceInfoVo.OrderDelivery deliveryVo = new ShopInvoiceInfoVo.OrderDelivery();
                            BeanUtils.copyProperties(delivery, deliveryVo);

                            if (ObjectUtils.isNotEmpty(delivery.getExpressCompanyId())) {
                                deliveryVo.setCompany(companyMap.get(delivery.getExpressCompanyId()));
                            }

                            goodsVo.setOrderDelivery(deliveryVo);
                        }
                    }

                    return goodsVo;
                }).toList();

                vo.setOrderGoods(goodsVoList);
            }

            if (StringUtils.isNotEmpty(vo.getTakerStoreId()) && storeMap.containsKey(Integer.parseInt(vo.getTakerStoreId()))) {
                ShopStore shopStore = storeMap.get(Integer.parseInt(vo.getTakerStoreId()));
                ShopOrderListVo.Store store = new ShopOrderListVo.Store();
                BeanUtils.copyProperties(shopStore, store);
                vo.setStore(store);
            }

            vo.setExpireTime(vo.getCreateTime() + orderConfig.getCloseOrderInfo().getCloseLength() * 60);

            if (!item.getOutTradeNo().isEmpty() && payMap.containsKey(item.getOutTradeNo())) {
                Pay pay = payMap.get(item.getOutTradeNo());
                ShopOrderListVo.Pay payVo = new ShopOrderListVo.Pay();
                BeanUtil.copyProperties(pay, payVo);
                vo.setPay(payVo);
            }

            if (invoiceIdSet.contains(item.getInvoiceId())) {
                ShopOrderListVo.Invoice invoiceVo = new ShopOrderListVo.Invoice();
                invoiceVo.setId(item.getInvoiceId());
                vo.setInvoice(invoiceVo);
            }

            if (ObjectUtils.isNotEmpty(vo.getInvoice()) ||
                    !Arrays.asList(OrderStatusEnum.WAIT_DELIVERY.getStatus(), OrderStatusEnum.WAIT_PAY.getStatus(), OrderStatusEnum.FINISH.getStatus()).contains(Integer.parseInt(vo.getStatus())) ||
                    vo.getOrderMoney().compareTo(BigDecimal.ZERO) == 0) {
                vo.setIsCanInvoice(0);
            }

            list.add(vo);
        }

        try {
            IsTradeManagedVo resultIsTradeManaged = coreWeappDeliveryService.getIsTradeManaged(siteId);
            if (resultIsTradeManaged.getIsTradeManaged()){
                result.setIsTradeManaged(true);
                PayChannel payChannel = corePayChannelService.find(siteId, "wechatpay", "weapp");
                if (payChannel != null && !"[]".equals(payChannel.getConfig())){
                    JSONObject config = JSONUtil.parseObj(payChannel.getConfig());
                    result.setMchId(config.getStr("mch_id", ""));
                }
            }
        } catch (Exception e) {
            log.error("查询小程序是否已开通发货信息管理服务失败:{}", e.getMessage());
        }
        result.setTotal(iPage.getTotal());
        result.setData(list);
        return result;
    }


    /**
     * 订单详情
     *
     * @param orderId 主键
     * @return ShopOrderInfoVo
     */
    @Override
    public ShopOrderInfoVo detail(Integer orderId) {
        Integer siteId = RequestUtils.siteId();
        ShopOrder model = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .eq("order_id", orderId)
                        .eq("site_id", siteId)
                        .eq("member_id", RequestUtils.memberId())
                        .select("buyer_ask_delivery_time,relate_id,activity_type,point,order_id,site_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money,is_evaluate,form_record_id"));

        Assert.notNull(model, "订单不存在");

        ShopOrderInfoVo vo = new ShopOrderInfoVo();
        BeanUtils.copyProperties(model, vo);

        if (model.getInvoiceId() > 0){
            boolean exists = shopInvoiceMapper.exists(new LambdaQueryWrapper<ShopInvoice>().eq(ShopInvoice::getId, model.getInvoiceId()));
            if (exists){
                ShopOrderListVo.Invoice invoiceVo = new ShopOrderListVo.Invoice();
                invoiceVo.setId(model.getInvoiceId());
                vo.setInvoice(invoiceVo);
            }
        }
        if (ObjectUtils.isNotEmpty(vo.getInvoice()) ||
                !Arrays.asList(OrderStatusEnum.WAIT_DELIVERY.getStatus(), OrderStatusEnum.WAIT_PAY.getStatus(), OrderStatusEnum.FINISH.getStatus()).contains(Integer.parseInt(vo.getStatus()))) {
            vo.setIsCanInvoice(0);
        }
        // 查询订单项
        List<ShopOrderInfoVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", vo.getOrderId())), ShopOrderInfoVo.OrderGoods.class);
        //单位map
        Set<Integer> goodsIds = orderGoods.stream().map(ShopOrderInfoVo.OrderGoods::getGoodsId).collect(Collectors.toSet());
        Map<Integer, String> unitMap = !CollectionUtils.isEmpty(goodsIds) ?
                shopGoodsMapper.selectByIds(goodsIds)
                        .stream().collect(Collectors.toMap(ShopGoods::getGoodsId, ShopGoods::getUnit)) : Map.of();

        for (ShopOrderInfoVo.OrderGoods orderGood : orderGoods) {
            if (unitMap.containsKey(orderGood.getGoodsId())){
                orderGood.setUnit(unitMap.get(orderGood.getGoodsId()));
            }
            if (StringUtils.isNotEmpty(orderGood.getExtend())){
                JSONObject extend = JSONUtil.parseObj(orderGood.getExtend());
                if (extend.containsKey("is_impulse_buy") && Objects.equals(extend.getInt("is_impulse_buy", 0), 1)){
                    Integer impulseBuyGoodsNum = extend.getInt("impulse_buy_goods_num", 0);
                    BigDecimal impulseBuyPriceTotal = extend.getBigDecimal("impulse_buy_price", BigDecimal.ZERO);
                    Integer goodsNum = orderGood.getNum();
                    BigDecimal goodsPrice = orderGood.getPrice();

                    String impulseBuyTips = "";

                    if (impulseBuyGoodsNum > 0) {
                        BigDecimal impulseBuyPrice = impulseBuyPriceTotal.divide(BigDecimal.valueOf(impulseBuyGoodsNum), 2, RoundingMode.HALF_UP);

                        if (impulseBuyGoodsNum == 1) {
                            impulseBuyTips = "第1" + orderGood.getUnit() + impulseBuyPrice + "元";
                        } else {
                            impulseBuyTips = "第1-" + impulseBuyGoodsNum + orderGood.getUnit() + impulseBuyPrice + "元";

                            if (goodsNum > impulseBuyGoodsNum) {
                                if (impulseBuyGoodsNum + 1 == goodsNum) {
                                    impulseBuyTips += " 第" + (impulseBuyGoodsNum + 1) + orderGood.getUnit() + goodsPrice + "元";
                                } else {
                                    impulseBuyTips += " 第" + (impulseBuyGoodsNum + 1) + "-" + goodsNum + orderGood.getUnit() + goodsPrice + "元";
                                }
                            }
                        }
                    } else {
                        impulseBuyTips = "第1-" + goodsNum + "件" + goodsPrice + "元";
                    }
                    orderGood.setImpulseBuyInfo(impulseBuyTips);
                    coreManjianService.getOrderGoodsGiveInfo(orderGood, orderGoods, orderGood.getSiteId(), orderGood.getMemberId());
                }
            };
        }
        vo.setOrderGoods(orderGoods);

        // 查询订单关闭配置
        OrderConfigVo orderConfig = coreOrderConfigService.getConfig(RequestUtils.siteId());
        vo.setExpireTime(vo.getCreateTime() + orderConfig.getCloseOrderInfo().getCloseLength() * 60);

        // 查询订单支付信息
        if (!vo.getOutTradeNo().isEmpty()) {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
            if (pay != null) {
                ShopOrderInfoVo.Pay payVo = new ShopOrderInfoVo.Pay();
                BeanUtil.copyProperties(pay, payVo);
                if (!vo.getMemberId().equals(pay.getMainId())){
                    Member member = memberMapper.selectById(pay.getMainId());
                    payVo.setPayMember(member.getNickname());
                    payVo.setPayMemberHeadimg(member.getHeadimg());
                }
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

        // 查询订单包裹信息(物流和自提点)
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()) || vo.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
            List<ShopOrderInfoVo.OrderDelivery> orderDelivery = CollectUtils.convert(shopOrderDeliveryMapper.selectList(new QueryWrapper<ShopOrderDelivery>().eq("order_id", vo.getOrderId())), ShopOrderInfoVo.OrderDelivery.class);
            vo.setOrderDelivery(orderDelivery);
//            if(vo.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())){
//                JSONObject thirdPartyConfig = coreLocalDeliveryService.getConfig(siteId);
//                for (ShopOrderInfoVo.OrderDelivery delivery : orderDelivery) {
//                    if (StringUtils.isNotEmpty(delivery.getDeliveryType())){
//                        //todo 之后实现
//                    }
//                }
//            }
        }

        //查询订单优惠信息
        List<ShopOrderDiscount> shopOrderDiscountList = shopOrderDiscountMapper.selectList(new QueryWrapper<ShopOrderDiscount>().eq("order_id", vo.getOrderId()));
        BigDecimal couponMoney = BigDecimal.ZERO;
        BigDecimal manjianDiscountMoney = BigDecimal.ZERO;
        if (ObjectUtil.isNotEmpty(shopOrderDiscountList)) {
            for (ShopOrderDiscount shopOrderDiscount : shopOrderDiscountList) {
                if (shopOrderDiscount.getDiscountType().equals("coupon")) {
                    couponMoney = couponMoney.add(shopOrderDiscount.getMoney());
                }
                if (shopOrderDiscount.getDiscountType().equals("manjian")) {
                    manjianDiscountMoney = manjianDiscountMoney.add(shopOrderDiscount.getMoney());
                }
            }
            vo.setOrderDiscount(shopOrderDiscountList);
        }
        vo.setCouponMoney(couponMoney);
        vo.setManjianDiscountMoney(manjianDiscountMoney);

        try {
            IsTradeManagedVo resultIsTradeManaged = coreWeappDeliveryService.getIsTradeManaged(siteId);
            if (resultIsTradeManaged.getIsTradeManaged()){
                vo.setIsTradeManaged(true);
                PayChannel payChannel = corePayChannelService.find(siteId, "wechatpay", "weapp");
                if (payChannel != null && !"[]".equals(payChannel.getConfig())){
                    JSONObject config = JSONUtil.parseObj(payChannel.getConfig());
                    vo.setMchId(config.getStr("mch_id", ""));
                }
            }
        } catch (Exception e) {
            log.error("查询小程序是否已开通发货信息管理服务失败:{}", e.getMessage());
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
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_TAKE.getStatus().toString()))
            throw new CommonException("只有待收货的订单才可以收货");

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
        if (delivery == null) return new DeliveryPackageVo();

        DeliveryPackageVo vo = new DeliveryPackageVo();
        BeanUtil.copyProperties(delivery, vo);

        DeliveryPackageVo.Company companyVo = new DeliveryPackageVo.Company();
        ShopDeliveryCompany company = shopDeliveryCompanyMapper.selectById(delivery.getExpressCompanyId());
        BeanUtil.copyProperties(company, companyVo);
        vo.setCompany(companyVo);

        List<DeliveryPackageVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("delivery_id", delivery.getId())), DeliveryPackageVo.OrderGoods.class);
        vo.setOrderGoods(orderGoods);

        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()) && !vo.getSubDeliveryType().equals(OrderDeliveryTypeEnum.NONE_EXPRESS.getType())) {
            DeliverySearchParam deliverySearchParam = new DeliverySearchParam();
            deliverySearchParam.setExpressNo(companyVo.getExpressNo());
            deliverySearchParam.setLogisticNo(delivery.getExpressNumber());
            deliverySearchParam.setMobile(searchParam.getMobile());
            deliverySearchParam.setCompanyId(company.getCompanyId());
            vo.setTraces(coreOrderService.deliverySearch(RequestUtils.siteId(), deliverySearchParam));
        }

        return vo;
    }

    @Override
    public Map<String, BigDecimal> calculateInvoiceMoney(List<Integer> tradeIds) {
        if (CollectionUtils.isEmpty(tradeIds)) {
            throw new ApiException("参数异常");
        }
        List<ShopOrder> shopOrders = shopOrderMapper.selectList(new LambdaQueryWrapper<ShopOrder>()
                .select(ShopOrder::getOrderMoney)
                .in(ShopOrder::getOrderId, tradeIds)
                .eq(ShopOrder::getInvoiceId, 0));
        if (CollectionUtils.isEmpty(shopOrders)) {
            Map<String, BigDecimal> data = new HashMap<>();
            data.put("invoice_money", BigDecimal.ZERO);
            return data;
        }
        BigDecimal invoiceMoney = shopOrders.stream().map(ShopOrder::getOrderMoney).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> data = new HashMap<>();
        data.put("invoice_money", invoiceMoney);
        return data;
    }

}
