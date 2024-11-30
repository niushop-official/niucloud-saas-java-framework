package com.niu.shop.service.core.order.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.enums.coupon.CouponTypeEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.event.order.OrderCreateAfterEvent;
import com.niu.shop.event.order.ShopOrderDiscountCreateEventDefiner;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.shop.service.core.delivery.ICoreExpressService;
import com.niu.shop.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.shop.service.core.delivery.ICoreStoreService;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import com.niu.shop.service.core.order.ICoreOrderDiscountService;
import com.niu.shop.service.core.order.ICoreOrderEventService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderPayParam;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class OrderCreateHelper {

    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    @Resource
    ICoreStoreService coreStoreService;

    @Resource
    ICoreExpressService coreExpressService;

    @Resource
    ICoreLocalDeliveryService coreLocalDeliveryService;

    @Resource
    ShopStoreMapper shopStoreMapper;

    @Resource
    MemberAddressMapper memberAddressMapper;

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;

    @Resource
    ShopCouponGoodsMapper shopCouponGoodsMapper;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;

    @Resource
    ICoreOrderDiscountService coreOrderDiscountService;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Resource
    ICoreOrderService coreOrderService;

    /**
     * @param orderKey
     * @return
     */
    protected <T extends OrderCreateDataVo> T loadOrderVo(String orderKey, Class<T> clazz) {
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get(RequestUtils.memberId() + "_member_order_" + orderKey);
        if (data == null) throw new CommonException("订单数据已失效");

        return JSONUtil.toBean(JSONUtil.parseObj(data), clazz);
    }

    /**
     * 获取订单数据
     *
     * @param orderKey
     * @return
     */
    protected JSONObject loadOrderData(String orderKey) {
        Cached cache = CacheFactory.getCacheOperator();
        Object data = cache.get(RequestUtils.memberId() + "_member_order_" + orderKey);
        if (data == null) throw new CommonException("订单数据已失效");

        return JSONUtil.parseObj(data);
    }

    /**
     * 缓存订单数据
     *
     * @param vo
     */
    protected <T extends OrderCreateDataVo> void cacheOrderVo(T vo) {
        if (vo.getOrderKey().isEmpty()) vo.setOrderKey(createOrderNo());

        Cached cache = CacheFactory.getCacheOperator();
        cache.put(RequestUtils.memberId() + "_member_order_" + vo.getOrderKey(), JSONUtil.parseObj(vo).toString());
    }

    /**
     * 移除订单数据缓存
     *
     * @param orderKey
     */
    protected void removeOrderVo(String orderKey) {
        Cached cache = CacheFactory.getCacheOperator();
        cache.remove(RequestUtils.memberId() + "_member_order_" + orderKey);
    }

    /**
     * 创建交易号
     *
     * @return
     */
    protected String createOrderNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 获取配送方式
     *
     * @param vo
     * @param param
     */
    protected void getDelivery(OrderCreateDataVo vo, OrderCalculateParam param) {
        OrderCreateDataVo.Basic basic = vo.getBasic();
        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            List<DeliveryConfigVo> deliveryList = coreDeliveryConfigService.getDeliveryList(param.getSiteId()).stream().filter(i -> i.getStatus().equals(1)).collect(Collectors.toList());
            vo.getDelivery().setDeliveryTypeList(deliveryList);
        }
    }

    /**
     * 计算运费
     *
     * @param vo
     * @param param
     */
    protected void calculateDelivery(OrderCreateDataVo vo, OrderCalculateParam param) {
        OrderCreateDataVo.Basic basic = vo.getBasic();
        String deliveryType = param.getDelivery().getDeliveryType();
        basic.setDeliveryMoney(new BigDecimal(0));

        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            if (deliveryType.isEmpty()) {
                if (vo.getDelivery().getDeliveryTypeList().size() > 0) {
                    deliveryType = vo.getDelivery().getDeliveryTypeList().get(0).getKey();
                }
            } else {
                if (vo.getDelivery().getDeliveryTypeList().size() == 0)
                    throw new CommonException("请选择正确的配送方式");
                List<String> typeList = vo.getDelivery().getDeliveryTypeList().stream().map(i -> i.getKey()).collect(Collectors.toList());
                if (!typeList.contains(deliveryType)) throw new CommonException("请选择正确的配送方式");
            }
        } else {
            deliveryType = OrderDeliveryTypeEnum.VIRTUAL.getType();
        }

        if (deliveryType.isEmpty()) {
            vo.getError().add("请选择正确的配送方式");
        } else {
            vo.getDelivery().setDeliveryType(deliveryType);

            selectTakeAddress(vo, param);

            if (deliveryType.equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                coreExpressService.calculate(vo);
            }
            if (deliveryType.equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                coreLocalDeliveryService.calculate(vo);
            }
            if (deliveryType.equals(OrderDeliveryTypeEnum.STORE.getType())) {
                coreStoreService.calculate(vo);
            }
        }
    }

    /**
     * 选中收货地址
     *
     * @param vo
     * @param param
     */
    protected void selectTakeAddress(OrderCreateDataVo vo, OrderCalculateParam param) {
        OrderCreateDataVo.Basic basic = vo.getBasic();
        OrderCreateDataVo.Delivery delivery = vo.getDelivery();
        String deliveryType = delivery.getDeliveryType();

        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            if (deliveryType.equals(OrderDeliveryTypeEnum.STORE.getType())) {
                if (param.getDelivery().getTakeStoreId() > 0) {
                    delivery.setTakeStore(shopStoreMapper.selectById(param.getDelivery().getTakeStoreId()));
                }
            } else {
                MemberAddress address = null;
                if (param.getDelivery().getTakeAddressId() > 0) {
                    address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>().eq("id", param.getDelivery().getTakeAddressId()).eq("member_id", param.getMemberId()));
                } else {
                    address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>().eq("is_default", 1).eq("member_id", param.getMemberId()));
                }

                if (deliveryType.equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType()) && ObjectUtil.isNotEmpty(address) && address.getLat().isEmpty()) {
                    vo.getError().add("所选的地址没有设置地图定位");
                }

                if (address == null) {
                    delivery.setTakeAddress(new String[]{});
                } else {
                    delivery.setTakeAddress(address);
                }
            }
        }
    }

    /**
     * 计算优惠
     *
     * @param vo
     * @param param
     */
    protected void calculateDiscount(OrderCreateDataVo vo, OrderCalculateParam param) {
        vo.getBasic().setDiscountMoney(new BigDecimal(0));

        for (Integer key : vo.getGoodsData().keySet()) {
            vo.getGoodsData().get(key).setDiscountMoney(new BigDecimal(0));
        }

        this.calculateCoupon(vo, param);
    }

    /**
     * 计算优惠券优惠
     *
     * @param vo
     * @param param
     */
    protected void calculateCoupon(OrderCreateDataVo vo, OrderCalculateParam param) {
        if (ObjectUtil.isNotEmpty(param.getDiscount().getCouponId()) && param.getDiscount().getCouponId() > 0) {
            ShopCouponMember memberCoupon = shopCouponMemberMapper.selectOne(new QueryWrapper<ShopCouponMember>()
                    .eq("member_id", vo.getMemberId())
                    .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                    .eq("id", param.getDiscount().getCouponId()));
            if (memberCoupon == null) throw new CommonException("优惠券已使用或已过期");
            if (memberCoupon.getExpireTime().compareTo(System.currentTimeMillis() / 1000) == -1)
                throw new CommonException("当前优惠券已过期");

            List<OrderCreateDataVo.Sku> matchGoods = new LinkedList<>();
            BigDecimal matchGoodsMoney = new BigDecimal(0);
            // 通用券
            if (memberCoupon.getType().equals(CouponTypeEnum.ALL.getType().toString())) {
                matchGoods = vo.getGoodsData().values().stream().collect(Collectors.toList());
                matchGoodsMoney = vo.getBasic().getGoodsMoney();
            }
            // 品类券
            if (memberCoupon.getType().equals(CouponTypeEnum.CATEGORY.getType().toString())) {
                List<Integer> categoryIds = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>()
                        .eq("coupon_id", memberCoupon.getCouponId())
                        .select("category_id")).stream().map(i -> i.getCategoryId()).collect(Collectors.toList());

                for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                    List<Integer> goodsCategory = JSONUtil.parseArray(sku.getGoods().getGoodsCategory()).toList(Integer.class);
                    boolean hasIntersection = !goodsCategory.retainAll(categoryIds);
                    if (!hasIntersection) {
                        matchGoods.add(sku);
                        matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney().subtract(sku.getDiscountMoney()));
                    }
                }
            }
            // 商品券
            if (memberCoupon.getType().equals(CouponTypeEnum.GOODS.getType().toString())) {
                List<Integer> goodsIds = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>()
                        .eq("coupon_id", memberCoupon.getCouponId())
                        .select("goods_id")).stream().map(i -> i.getGoodsId()).collect(Collectors.toList());

                for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                    if (goodsIds.contains(sku.getGoodsId())) {
                        matchGoods.add(sku);
                        matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney().subtract(sku.getDiscountMoney()));
                    }
                }
            }

            if (matchGoods.size() == 0) {
                vo.getError().add("当前优惠券在本单不可用");
            } else if (matchGoodsMoney.compareTo(memberCoupon.getMinConditionMoney()) == -1) {
                vo.getError().add("未达到当前优惠券的最低使用条件");
            } else {
                BigDecimal couponMoney = matchGoodsMoney.compareTo(memberCoupon.getPrice()) == -1 ? matchGoodsMoney : memberCoupon.getPrice();
                BigDecimal discountMoney = couponMoney;

                for (int i = 0; i < matchGoods.size(); i++) {
                    OrderCreateDataVo.Sku sku = matchGoods.get(i);
                    OrderCreateDataVo.Sku skuVo = vo.getGoodsData().get(sku.getSkuId());

                    if (i < matchGoods.size() - 1) {
                        BigDecimal tempCouponMoney = sku.getGoodsMoney().subtract(sku.getDiscountMoney()).divide(matchGoodsMoney, 2, BigDecimal.ROUND_HALF_UP).multiply(couponMoney);
                        skuVo.setDiscountMoney(sku.getDiscountMoney().add(tempCouponMoney));
                        couponMoney = couponMoney.subtract(tempCouponMoney);
                    } else {
                        skuVo.setDiscountMoney(sku.getDiscountMoney().add(couponMoney));
                    }
                }

                vo.getBasic().setDiscountMoney(vo.getBasic().getDiscountMoney().add(discountMoney));

                OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                discount.setMatchGoodsIds(matchGoods.stream().map(i -> i.getSkuId()).toArray(Integer[]::new));
                discount.setNum(1);
                discount.setType("discount");
                discount.setMoney(discountMoney);
                discount.setDiscountType("coupon");
                discount.setDiscountTypeId(memberCoupon.getId());
                discount.setTitle(memberCoupon.getTitle());
                discount.setContent("");
                vo.getDiscount().put("coupon", discount);
            }
        } else {
            if (vo.getDiscount().get("coupon") != null) vo.getDiscount().remove("coupon");
        }
    }

    /**
     * 使用优惠
     *
     * @param vo
     */
    protected void useDiscount(OrderCreateDataVo vo) {
        if (ObjectUtil.isNotEmpty(vo.getDiscount())) {
            List<ShopOrderDiscount> discountList = new LinkedList<>();
            for (OrderCreateDataVo.Discount item : vo.getDiscount().values()) {
                ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent event = new ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEvent();
                event.setSiteId(vo.getSiteId());
                event.addAppSign("shop");
                event.setName("ShopOrderDiscountCreateEvent");
                event.setDiscount(item);
                event.setOrderCreateData(vo);
                List<ShopOrderDiscountCreateEventDefiner.ShopOrderDiscountCreateEventResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);

                if (eventData.size() == 0) {
                    switch (item.getDiscountType()) {
                        case "coupon":
                            coreCouponMemberService.use(item.getDiscountTypeId(), vo.getOrderId());
                            break;
                    }
                }

                ShopOrderDiscount orderDiscount = new ShopOrderDiscount();
                BeanUtil.copyProperties(item, orderDiscount);
                orderDiscount.setOrderId(vo.getOrderId());
                orderDiscount.setCreateTime(System.currentTimeMillis() / 1000);
                orderDiscount.setSiteId(vo.getSiteId());
                discountList.add(orderDiscount);
            }

            coreOrderDiscountService.inserAll(discountList);
        }
    }

    /**
     * 创建订单
     *
     * @param vo
     * @return
     */
    @Transactional
    protected OrderCreateResultVo createOrder(OrderCreateDataVo vo, ShopOrder order, List<ShopOrderGoods> orderGoods) {
        // 添加订单表
        order.setCreateTime(System.currentTimeMillis() / 1000);
        shopOrderMapper.insert(order);
        vo.setOrderId(order.getOrderId());

        // 添加订单项表
        for (ShopOrderGoods item : orderGoods) {
            item.setOrderId(order.getOrderId());
            item.setStatus(1);
            shopOrderGoodsMapper.insert(item);
        }

        // 使用优惠
        this.useDiscount(vo);

        coreOrderEventService.orderCreateAfter(vo);

        // 订单创建之后事件
        OrderCreateAfterEvent event = new OrderCreateAfterEvent();
        event.setSiteId(vo.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderCreateAfterEvent");
        event.setOrderData(vo);
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);

        // 清除订单缓存
        removeOrderVo(vo.getOrderKey());

        if (order.getOrderMoney().equals(BigDecimal.ZERO)) {
            OrderPayParam orderPayParam = new OrderPayParam();
            orderPayParam.setTradeId(order.getOrderId());
            orderPayParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
            orderPayParam.setMainId(order.getMemberId());
            coreOrderService.pay(orderPayParam);
        }

        OrderCreateResultVo result = new OrderCreateResultVo();
        result.setTradeType(vo.getOrderType());
        result.setOrderId(order.getOrderId());
        return result;
    }
}
