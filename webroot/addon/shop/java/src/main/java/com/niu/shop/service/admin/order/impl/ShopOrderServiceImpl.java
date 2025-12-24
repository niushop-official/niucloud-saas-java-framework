package com.niu.shop.service.admin.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.verify.Verify;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.diy_form.DiyFormRecordsFieldsMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyOrderVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.shop.entity.delivery.ShopDeliveryLocalDelivery;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.*;
import com.niu.shop.enums.order.*;
import com.niu.shop.mapper.delivery.ShopDeliveryLocalDeliveryMapper;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.*;
import com.niu.shop.service.admin.delivery.IDeliveryService;
import com.niu.shop.service.admin.order.IShopOrderService;
import com.niu.shop.service.admin.order.param.*;
import com.niu.shop.service.admin.order.vo.EditDeliveryVo;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderListVo;
import com.niu.shop.service.admin.order.vo.ShopOrderLogListVo;
import com.niu.shop.service.core.delivery.ICoreStoreService;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.LocalInfoVo;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsStatParam;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.order.ICoreOrderLogService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.AddOrderLogParam;
import com.niu.shop.service.core.order.param.OrderCloseParam;
import com.niu.shop.service.core.order.param.OrderPayParam;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import com.niu.shop.service.core.shop.param.StatParam;
import lombok.extern.log4j.Log4j2;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 订单实现
 */
@Log4j2
@Service
public class ShopOrderServiceImpl implements IShopOrderService {

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    PayMapper payMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ShopStoreMapper shopStoreMapper;

    @Resource
    ShopOrderDeliveryMapper shopOrderDeliveryMapper;

    @Resource
    ShopOrderLogMapper shopOrderLogMapper;

    @Resource
    ICoreOrderService coreOrderService;

    @Resource
    ICoreOrderLogService coreOrderLogService;

    @Resource
    ICorePayService corePayService;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ICoreManjianService coreManjianService;

    @Resource
    private IDeliveryService deliveryService;

    @Resource
    private ShopDeliveryLocalDeliveryMapper shopDeliveryLocalDeliveryMapper;

    @Resource
    private ICoreStoreService coreStoreService;

    @Resource
    private ShopOrderDiscountMapper shopOrderDiscountMapper;

    @Resource
    private VerifyMapper verifyMapper;

    @Resource
    private ShopOrderRefundMapper shopOrderRefundMapper;

    @Resource
    private ICoreStatService coreStatService;

    @Resource
    private ICoreGoodsStatService coreGoodsStatService;

    @Resource
    private DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;

    /**
     * 订单列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    @Override
    public PageResult<ShopOrderListVo> page(PageParam pageParam, ShopOrderSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopOrder> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("o")
                .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("o.point,activity_type,order_id,order_no,order_type,order_from,o.out_trade_no,o.status,o.member_id,ip,goods_money,delivery_money,order_money,o.create_time,o.pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,o.pay_money, o.relate_source,o.relate_order_id");
        queryWrapper.eq("o.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("o.create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getSearchType()) && ObjectUtil.isNotEmpty(searchParam.getSearchName())) {
            queryWrapper.like("o." + searchParam.getSearchType(), searchParam.getSearchName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("o.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderFrom()))
            queryWrapper.eq("o.order_from", searchParam.getOrderFrom());
        if (ObjectUtil.isNotEmpty(searchParam.getPayType())) queryWrapper.eq("p.type", searchParam.getPayType());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime()))
            QueryMapperUtils.buildByTime(queryWrapper, "o.create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime()))
            QueryMapperUtils.buildByTime(queryWrapper, "p.pay_time", searchParam.getPayTime());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("m.member_no", searchParam.getKeyword())
                    .or().like("m.nickname", searchParam.getKeyword())
                    .or().like("m.username", searchParam.getKeyword())
                    .or().like("m.mobile", searchParam.getKeyword()));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getActivityType()))
            queryWrapper.eq("o.activity_type", searchParam.getActivityType());

        IPage<ShopOrderListVo> iPage = shopOrderMapper.selectJoinPage(new Page<>(page, limit), ShopOrderListVo.class, queryWrapper);
        for (ShopOrderListVo vo : iPage.getRecords()) {
            List<ShopOrderListVo.OrderGoods> orderGoods = shopOrderGoodsMapper.selectJoinList(ShopOrderListVo.OrderGoods.class,new MPJQueryWrapper<ShopOrderGoods>()
                .setAlias("og")
                .select("og.*,sod.express_number,sdc.company_name as express_company_name")
                .leftJoin("?_shop_order_delivery sod ON og.delivery_id = sod.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_shop_delivery_company sdc ON sod.express_company_id = sdc.company_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("og.order_id", vo.getOrderId())
            );
            for (ShopOrderListVo.OrderGoods orderGoodsVo : orderGoods) {
                if (orderGoodsVo.getDeliveryId() != null) {
                    ShopOrderListVo.DeliveryInfo deliveryInfo = new ShopOrderListVo.DeliveryInfo();
                    BeanUtil.copyProperties(orderGoodsVo, deliveryInfo);
                    orderGoodsVo.setDeliveryInfo(deliveryInfo);
                }
            }
            vo.setOrderGoods(orderGoods);

            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
            BeanUtil.copyProperties(member, memberVo);
            vo.setMember(memberVo);

            if (!vo.getOutTradeNo().isEmpty()) {
                Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
                if (pay != null) {
                    ShopOrderListVo.Pay payVo = new ShopOrderListVo.Pay();
                    BeanUtil.copyProperties(pay, payVo);
                    vo.setPay(payVo);
                }
            }
        }
        List<ShopOrderListVo> result = getOrderRelateSourceInfo(iPage.getRecords());
        return PageResult.build(page, limit, iPage.getTotal(), result);
    }

    private List<ShopOrderListVo> getOrderRelateSourceInfo(List<ShopOrderListVo> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        log.info("开始获取订单关联源信息，订单数量: {}", list.size());
        try {
            Map<String, List<Map<String, Object>>> relateActivityList = list.stream()
                    .collect(Collectors.groupingBy(
                            ShopOrderListVo::getRelateSource,
                            Collectors.mapping(order -> Map.of(
                                    "order_id", order.getRelateSource() + order.getOrderId(),
                                    "relate_order_id", order.getRelateOrderId()
                            ), Collectors.toList())
                    ));
            CommonEventDefiner.CommonEvent sendEvent = new CommonEventDefiner.CommonEvent();
            sendEvent.setEventName("ThirdAddonGetOrderActive");
            sendEvent.setData(relateActivityList);
            List<CommonEventDefiner.CommonEventResult> activeResList = EventAndSubscribeOfPublisher.publishAndCallback(sendEvent);
            Map<String, Object> resultMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(activeResList)){
                for (CommonEventDefiner.CommonEventResult result : activeResList) {
                    resultMap.putAll(result.getDataMap());
                }
            }
            log.info("获取到活动信息数量: {}", resultMap.size());
            return list.stream()
                    .map(order -> {
                        String orderId = order.getRelateSource() + order.getOrderId().toString();
                        if (resultMap.containsKey(orderId)) {
                            Object activeInfo = resultMap.get(orderId);
                            order.setRelateActiveInfo(activeInfo);
                        } else {
                            order.setRelateActiveInfo(new HashMap<>());
                        }
                        return order;
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取订单关联源信息失败", e);
            // 发生异常时返回原列表，但设置空的活动信息
            return list.stream()
                    .peek(order -> order.setRelateActiveInfo(new HashMap<>()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 订单详情
     *
     * @param id 主键
     * @return ShopOrderInfoVo
     */
    @Override
    public ShopOrderInfoVo detail(Integer id) {
        ShopOrder model = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .eq("order_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .select("activity_type,point,order_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,buyer_ask_delivery_time,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money,form_record_id"));

        Assert.notNull(model, "订单不存在");

        ShopOrderInfoVo vo = new ShopOrderInfoVo();
        BeanUtils.copyProperties(model, vo);

        MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
        BeanUtil.copyProperties(member, memberVo);
        vo.setMember(memberVo);

        // 查询订单项
        List<ShopOrderInfoVo.OrderGoods> orderGoods = shopOrderGoodsMapper.selectJoinList(ShopOrderInfoVo.OrderGoods.class,new MPJQueryWrapper<ShopOrderGoods>()
            .setAlias("og")
            .select("og.*,g.unit,sod.express_number,sdc.company_name as express_company_name")
            .leftJoin("?_shop_goods g ON og.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
            .leftJoin("?_shop_order_delivery sod ON og.delivery_id = sod.id".replace("?_", GlobalConfig.tablePrefix))
            .leftJoin("?_shop_delivery_company sdc ON sod.express_company_id = sdc.company_id".replace("?_", GlobalConfig.tablePrefix))
            .eq("og.order_id", vo.getOrderId())
        );

        for (ShopOrderInfoVo.OrderGoods orderGoodsVo : orderGoods) {
            orderGoodsVo.setUnit(orderGoodsVo.getUnit().equals("") ? "件" : orderGoodsVo.getUnit());
            if (orderGoodsVo.getDeliveryId() != null) {
                ShopOrderInfoVo.DeliveryInfo deliveryInfo = new ShopOrderInfoVo.DeliveryInfo();
                BeanUtil.copyProperties(orderGoodsVo, deliveryInfo);
                orderGoodsVo.setDeliveryInfo(deliveryInfo);
            }
            if (StringUtils.isNotEmpty(orderGoodsVo.getExtend())) {
                JSONObject extend = JSONUtil.parseObj(orderGoodsVo.getExtend());
                if (extend.containsKey("is_impulse_buy") && extend.getInt("is_impulse_buy", 0) == 1) {
                    Integer impulseBuyGoodsNum = extend.getInt("impulse_buy_goods_num", 0);
                    BigDecimal impulseBuyPriceTotal = extend.getBigDecimal("impulse_buy_price", BigDecimal.ZERO);
                    Integer goodsNum = orderGoodsVo.getNum();
                    BigDecimal goodsPrice = orderGoodsVo.getPrice();

                    String impulseBuyTips = "";

                    if (impulseBuyGoodsNum > 0) {
                        BigDecimal impulseBuyPrice = impulseBuyPriceTotal.divide(BigDecimal.valueOf(impulseBuyGoodsNum), 2, RoundingMode.HALF_UP);

                        if (impulseBuyGoodsNum == 1) {
                            impulseBuyTips = "第1件" + impulseBuyPrice + "元";
                        } else {
                            impulseBuyTips = "第1-" + impulseBuyGoodsNum + "件" + impulseBuyPrice + "元";

                            if (goodsNum > impulseBuyGoodsNum) {
                                if (impulseBuyGoodsNum + 1 == goodsNum) {
                                    impulseBuyTips += " 第" + (impulseBuyGoodsNum + 1) + "件" + goodsPrice + "元";
                                } else {
                                    impulseBuyTips += " 第" + (impulseBuyGoodsNum + 1) + "-" + goodsNum + "件" + goodsPrice + "元";
                                }
                            }
                        }
                    } else {
                        impulseBuyTips = "第1-" + goodsNum + "件" + goodsPrice + "元";
                    }
                    orderGoodsVo.setImpulseBuyInfo(Map.of("show_tips", impulseBuyTips));
                }
            }
//            coreManjianService.getOrderGoodsGiveInfo(orderGoodsVo, orderGoods, orderGoodsVo.getSiteId(), orderGoodsVo.getMemberId());
        }

        vo.setOrderGoods(orderGoods);

        //orderDiscount 订单优惠表
        List<ShopOrderDiscount> shopOrderDiscountList = shopOrderDiscountMapper.selectList(new QueryWrapper<ShopOrderDiscount>()
                .eq("order_id", id)
                .eq("site_id", RequestUtils.siteId()).select("order_id,discount_type,money,content"));
        if(ObjectUtil.isNotNull(shopOrderDiscountList) && shopOrderDiscountList.size()>0){
            vo.setOrderDiscount(shopOrderDiscountList);
            double couponMoney = 0.0;
            double manjianDiscountMoney = 0.0;
            for (ShopOrderDiscount item : shopOrderDiscountList) {
                String discountType = item.getDiscountType();
                Double money = item.getMoney().doubleValue();
                // 判断优惠券类型
                if ("coupon".equals(discountType)) {
                    couponMoney += money;
                }
                // 判断满减类型
                else if ("manjian".equals(discountType)) {
                    manjianDiscountMoney += money;
                }
            }
            vo.setCouponMoney(Double.toString(couponMoney));
            vo.setManjianDiscountMoney(Double.toString(manjianDiscountMoney));
        }else{
            vo.setOrderDiscount(new ArrayList<>());
        }

        // 查询订单支付信息
        if (!vo.getOutTradeNo().isEmpty()) {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
            if (pay != null) {
                ShopOrderInfoVo.Pay payVo = new ShopOrderInfoVo.Pay();
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

            MPJQueryWrapper<Verify> verifyQueryWrapper = new MPJQueryWrapper<>();
            verifyQueryWrapper.setAlias("v").leftJoin("?_member m ON m.member_id = v.verifier_member_id".replace("?_", GlobalConfig.tablePrefix));
            verifyQueryWrapper.select("v.id, v.code, v.verifier_member_id, m.member_id, m.nickname");
            verifyQueryWrapper.eq("v.site_id", RequestUtils.siteId());
            verifyQueryWrapper.eq("v.relate_tag", id);
            verifyQueryWrapper.eq("type","shopPickUpOrder");
            VerifyOrderVo verifyOrderVo = verifyMapper.selectJoinOne(VerifyOrderVo.class,verifyQueryWrapper);
            if(ObjectUtil.isNotNull(verifyOrderVo)){
                vo.setVerifyCode(verifyOrderVo.getCode());
                vo.setVerifierMember(StringUtils.toUnderScoreCase(JSONUtil.toJsonStr(verifyOrderVo)));
            }
        }

        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            List<ShopOrderInfoVo.OrderDelivery> orderDelivery = CollectUtils.convert(shopOrderDeliveryMapper.selectList(new QueryWrapper<ShopOrderDelivery>().eq("order_id", vo.getOrderId())), ShopOrderInfoVo.OrderDelivery.class);
            vo.setOrderDelivery(orderDelivery);
        }


        List<ShopOrderLogListVo> orderLog = CollectUtils.convert(shopOrderLogMapper.selectList(new QueryWrapper<ShopOrderLog>().eq("order_id", vo.getOrderId()).orderByDesc("create_time")), ShopOrderLogListVo.class);
        vo.setOrderLog(orderLog);



        return vo;
    }

    /**
     * 关闭订单
     *
     * @param id
     */
    @Override
    public void close(Integer id) {
        OrderCloseParam closeParam = new OrderCloseParam();
        closeParam.setOrderId(id);
        closeParam.setSiteId(RequestUtils.siteId());
        closeParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        closeParam.setMainId(RequestUtils.uid());
        closeParam.setCloseType(OrderCloseTypeEnum.SHOP_CLOSE.getType());
        coreOrderService.close(closeParam);
    }

    /**
     * 设置商家备注
     *
     * @param param
     */
    @Override
    public void setShopRemark(SetShopRemarkParam param) {
        ShopOrder model = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .select("order_id,status")
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "订单不存在");

        model.setShopRemark(param.getShopRemark());
        shopOrderMapper.updateById(model);

        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(model.getOrderId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());
        logParam.setType(OrderLogActionEnum.ORDER_REMARK_ACTION.getAction());
        coreOrderLogService.add(logParam);
    }
    //getOrderCount转到stat
    /**
     * 订单调价
     *
     * @param param
     */
    @Override
    @Transactional
    public void editPrice(EditPriceParam param) {
        ShopOrder order = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .select("order_id,site_id,status,order_type,goods_money,delivery_money,order_money,discount_money")
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString()))
            throw new CommonException("只有待支付的订单可以改价");

        // 关闭已经发起的支付单据
        corePayService.closeByTrade(order.getSiteId(), order.getOrderType(), order.getOrderId());
        //判断配送费用
        if (param.getDeliveryMoney().compareTo(BigDecimal.ZERO) == -1) throw new CommonException("配送费用不能小于0");
        BigDecimal goodsMoney = new BigDecimal(0);
        List<ShopOrderGoods> orderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()).select("order_goods_id,order_goods_money,goods_money,price,discount_money,num"));
        BigDecimal oldDeliveryMoney = order.getDeliveryMoney();
        StringBuffer content = new StringBuffer("运费：从" + oldDeliveryMoney + "元 调整为" + param.getDeliveryMoney() + "元");
        for (ShopOrderGoods item : orderGoods) {
            if (ObjectUtil.isNotEmpty(param.getOrderGoodsData().get(item.getOrderGoodsId()))) {
                BigDecimal money = param.getOrderGoodsData().get(item.getOrderGoodsId()).getMoney();
                if (money.compareTo(BigDecimal.ZERO) != 0) {
                    ShopOrderGoods orderGoodsUpdate = new ShopOrderGoods();
                    orderGoodsUpdate.setOrderGoodsId(item.getOrderGoodsId());
                    orderGoodsUpdate.setOrderGoodsMoney(item.getOrderGoodsMoney().add(money));
                    if (orderGoodsUpdate.getOrderGoodsMoney().compareTo(BigDecimal.ZERO) == -1) {
                        throw new CommonException("订单项小计总额不能小于0");
                    }
                    orderGoodsUpdate.setGoodsMoney(orderGoodsUpdate.getOrderGoodsMoney().add(item.getDiscountMoney()));
                    orderGoodsUpdate.setPrice(orderGoodsUpdate.getGoodsMoney().divide(new BigDecimal(item.getNum()), 2, BigDecimal.ROUND_HALF_UP));
                    content.append(item.getGoodsName() + "：从原价" + item.getGoodsMoney() + "元 调整为" + orderGoodsUpdate.getGoodsMoney() + "元");
                    shopOrderGoodsMapper.updateById(orderGoodsUpdate);
                    goodsMoney = goodsMoney.add(orderGoodsUpdate.getGoodsMoney());
                    continue;
                }
            }
            goodsMoney = goodsMoney.add(item.getGoodsMoney());
        }

        // 修改订单表
        ShopOrder orderUpdate = new ShopOrder();
        orderUpdate.setOrderId(order.getOrderId());
        orderUpdate.setOrderMoney(goodsMoney.add(param.getDeliveryMoney()).subtract(order.getDiscountMoney()));
        if (orderUpdate.getOrderMoney().compareTo(BigDecimal.ZERO) == -1) orderUpdate.setOrderMoney(BigDecimal.ZERO);
        orderUpdate.setDeliveryMoney(param.getDeliveryMoney());
        orderUpdate.setGoodsMoney(goodsMoney);
        this.shopOrderMapper.updateById(orderUpdate);

        // 添加订单操作日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(OrderStatusEnum.WAIT_PAY.getStatus());
        logParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
        logParam.setMainId(RequestUtils.uid());
        logParam.setType(OrderLogActionEnum.ORDER_EDIT_PRICE_ACTION.getAction());
        logParam.setContent(content.toString());
        coreOrderLogService.add(logParam);

        // 如果调价后的订单金额为0
        if (orderUpdate.getOrderMoney().compareTo(BigDecimal.ZERO) == 0) {
            OrderPayParam payParam = new OrderPayParam();
            payParam.setTradeId(order.getOrderId());
            payParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
            payParam.setMainId(0);
            coreOrderService.pay(payParam);
        }
    }

    /**
     * 修改订单配送
     *
     * @param param
     */
    @Override
    public void editDelivery(EditDeliveryParam param) {
        EditDeliveryVo editDeliveryData = getEditDeliveryData(param);
        if (editDeliveryData.getErrorCode() < 0){
            throw new CommonException(editDeliveryData.getErrorMsg());
        }
        EditDeliveryParam deliveryData = editDeliveryData.getDeliveryData();


        ShopOrder model = new ShopOrder();
        BeanUtil.copyProperties(deliveryData, model);

        shopOrderMapper.updateById(model);
    }
    /**
     * 获取订单配送修改信息
     *
     * @param param
     * @return
     */
    @Override
    public EditDeliveryVo getEditDeliveryData(EditDeliveryParam param) {
        Integer siteId = RequestUtils.siteId();
        ShopOrder order = shopOrderMapper.selectOne(
                new LambdaQueryWrapper<ShopOrder>()
                        .eq(ShopOrder::getOrderId, param.getOrderId())
                        .eq(ShopOrder::getSiteId, siteId));

        Assert.notNull(order, "订单不存在");

        List<String> errorStatusList = List.of(OrderStatusEnum.WAIT_PAY.getStatus().toString(),
                OrderStatusEnum.WAIT_DELIVERY.getStatus().toString());
        if (!errorStatusList.contains(order.getStatus())) {
            throw new CommonException("只有待支付或待发货的订单可以修改配送地址");
        }
        if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) {
            throw new CommonException("虚拟商品订单不可以修改配送地址");
        }
        
        List<DeliveryConfigVo> deliveryList = deliveryService.getDeliveryList();
        DeliveryConfigVo selectedDelivery = deliveryList.stream().filter(item -> item.getKey().equals(param.getDeliveryType())).findFirst().orElse(null);

        EditDeliveryVo result = new EditDeliveryVo();
        result.setDeliveryData(param);
        BeanUtils.copyProperties(order, result);
        result.setErrorCode(1);
        result.setErrorMsg("");

        if (selectedDelivery == null) {
            result.setErrorCode(-1);
            result.setErrorMsg("不支持选择的配送方式");
            return result;
        } else if (selectedDelivery.getStatus() > 1) {
            result.setErrorCode(-1);
            result.setErrorMsg("商家未开启该配送方式");
            return result;
        }

        List<ShopOrderGoods> goodsList = shopOrderGoodsMapper.selectList(new LambdaQueryWrapper<ShopOrderGoods>()
                .eq(ShopOrderGoods::getSiteId, siteId)
                .eq(ShopOrderGoods::getGoodsType, "real")
                .eq(ShopOrderGoods::getOrderId, param.getOrderId()));
        Set<Integer> goodsIds = goodsList.stream().map(ShopOrderGoods::getGoodsId).collect(Collectors.toSet());
        List<ShopGoods> shopGoods = shopGoodsMapper.selectList(new LambdaQueryWrapper<ShopGoods>().in(ShopGoods::getGoodsId, goodsIds));

        List<ShopOrderGoods> orderGoods = shopGoods.stream().map(goods -> {
            ShopOrderGoods vo = new ShopOrderGoods();
            BeanUtils.copyProperties(goods, vo);

            // 检查商品是否支持该配送方式
            if (!goods.getDeliveryType().contains(param.getDeliveryType())) {
                result.setErrorCode(-1);
                result.setErrorMsg("当前商品不支持该配送方式");
            }
            return vo;
        }).collect(Collectors.toList());
        result.setOrderGoods(orderGoods);

        String unsupportedGoodsNames = "";
        if (result.getErrorCode() < 0) {
            unsupportedGoodsNames = orderGoods.stream()
                    .map(ShopOrderGoods::getGoodsName)
                    .collect(Collectors.joining(","));
        }
        if (!unsupportedGoodsNames.isEmpty()) {
            result.setErrorCode(-1);
            result.setErrorMsg(unsupportedGoodsNames + "当前商品不支持该配送方式");
            return result;
        }


        switch (selectedDelivery.getKey()) {
            case "express":
                if (StringUtils.isAnyBlank(
                        param.getTakerName(),
                        param.getTakerMobile(),
                        String.valueOf(param.getTakerProvince()),
                        String.valueOf(param.getTakerCity()),
                        String.valueOf(param.getTakerDistrict()),
                        param.getTakerAddress(),
                        param.getTakerFullAddress())) {
                    result.setErrorCode(-1);
                    result.setErrorMsg("地址信息有误");
                }
                break;

            case "local_delivery":
                if (StringUtils.isAnyBlank(
                        param.getTakerName(),
                        param.getTakerMobile(),
                        String.valueOf(param.getTakerProvince()),
                        String.valueOf(param.getTakerCity()),
                        String.valueOf(param.getTakerDistrict()),
                        param.getTakerAddress(),
                        param.getTakerFullAddress(),
                        param.getTakerLongitude(),
                        param.getTakerLatitude())) {
                    result.setErrorCode(-1);
                    result.setErrorMsg("地址信息有误");
                } else {
                    checkLocationInArea(result, param);
                }
                break;

            case "store":
                if (param.getTakeStoreId() == null) {
                    result.setErrorCode(-1);
                    result.setErrorMsg("地址信息有误");
                }
                break;

        }
        return result;
    }

    private void checkLocationInArea(EditDeliveryVo result, EditDeliveryParam param) {
        List<ShopDeliveryLocalDelivery> localConfig = shopDeliveryLocalDeliveryMapper.selectList(new LambdaQueryWrapper<ShopDeliveryLocalDelivery>().eq(ShopDeliveryLocalDelivery::getSiteId, RequestUtils.siteId()));
        if (localConfig.isEmpty()){
            result.setErrorCode(-1);
            result.setErrorMsg("商家未配置同城配送");
            return;
        }

        // 判断所在区域
        LocalInfoVo.Area locatedInArea = null;
        List<LocalInfoVo.Area> areaList = JSONUtil.toList(JSONUtil.parseArray(localConfig.get(0).getArea()), LocalInfoVo.Area.class);
        for (LocalInfoVo.Area area : areaList) {
            if ("radius".equals(area.getAreaType())) {
                // 圆形区域判断
                Double distance = DistanceCalculateUtils.calculateDistance(Double.parseDouble(param.getTakerLatitude()),
                        Double.parseDouble(param.getTakerLongitude()),
                        area.getAreaJson().getCenter().getLat().doubleValue(),
                        area.getAreaJson().getCenter().getLng().doubleValue());
                if (distance <= area.getAreaJson().getRadius()) {
                    locatedInArea = area;
                    break;
                }
            } else {
                // 多边形区域判断
                double[] polyLats = area.getAreaJson().getPaths().stream().mapToDouble(latlng -> latlng.getLat().doubleValue()).toArray();
                double[] polyLngs = area.getAreaJson().getPaths().stream().mapToDouble(latlng -> latlng.getLng().doubleValue()).toArray();
                if (DistanceCalculateUtils.isPointInPolygon(Double.parseDouble(param.getTakerLatitude()), Double.parseDouble(param.getTakerLongitude()), polyLats, polyLngs)) {
                    locatedInArea = area;
                    break;
                }
            }
        }

        if (locatedInArea == null) {
            result.setErrorCode(-1);
            result.setErrorMsg("不支持配送地址");
        }
    }

    /**
     * 删除订单
     *
     * @param param
     */
    @Override
    @Transactional
    public void delete(DeleteOrderParam param) {
        if (ObjectUtil.isEmpty(param.getOrderIds())) throw new CommonException("订单不存在");
        Integer siteId = RequestUtils.siteId();
        List<ShopOrder> orderList = shopOrderMapper.selectList(new QueryWrapper<ShopOrder>()
            .in("order_id", param.getOrderIds())
            .eq("site_id", siteId)
            .select("order_id,status,order_no,create_time")
        );
        if (orderList.isEmpty()) throw new CommonException("订单不存在");

        param.setOrderIds(orderList.stream().map(ShopOrder::getOrderId).collect(Collectors.toList()));

        List<String> errorOrder = orderList.stream().filter(order -> !OrderStatusEnum.CLOSE.getStatus().equals(Integer.valueOf(order.getStatus()))).map(i -> i.getOrderNo()).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(errorOrder)) throw new CommonException("删除订单中有未关闭订单：" + String.join(",", errorOrder));

        Map<Integer, List<ShopOrderGoods>> orderGoodsMap = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>()
            .in("order_id", param.getOrderIds())
            .eq("site_id", siteId)
            .select("site_id, order_id, goods_id, num")
        ).stream().collect(Collectors.groupingBy(ShopOrderGoods::getOrderId));

        // 删除订单表
        shopOrderMapper.delete(new QueryWrapper<ShopOrder>().in("order_id", param.getOrderIds()));

        // 订单日志表
        shopOrderLogMapper.delete(new QueryWrapper<ShopOrderLog>().in("order_id", param.getOrderIds()));

        // 删除订单商品表
        shopOrderGoodsMapper.delete(new QueryWrapper<ShopOrderGoods>().in("order_id", param.getOrderIds()));

        // 刪除退款表
        shopOrderRefundMapper.delete(new QueryWrapper<ShopOrderRefund>().in("order_id", param.getOrderIds()));

        //删除统计
        for (ShopOrder order : orderList) {
            List<ShopOrderGoods> orderGoodsList = orderGoodsMap.get(order.getOrderId());
            for (ShopOrderGoods orderGoods : orderGoodsList) {
                coreGoodsStatService.decStat(GoodsStatParam.builder()
                        .siteId(siteId)
                        .goodsId(orderGoods.getGoodsId())
                        .time(order.getCreateTime())
                        .saleNum(orderGoods.getNum())
                        .build());
            }
            coreStatService.decStat(StatParam.builder()
                    .siteId(siteId)
                    .time(order.getCreateTime())
                    .orderNum(1)
                    .build());
        }
    }
}
