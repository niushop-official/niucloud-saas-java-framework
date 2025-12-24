package com.niu.shop.service.core.order.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.MarketingActivityTypeEnum;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.enums.coupon.CouponTypeEnum;
import com.niu.shop.enums.goods.GoodsPriceTypeEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.event.order.OrderCreateAfterEvent;
import com.niu.shop.event.order.OrderDiscountCalculateEvent;
import com.niu.shop.event.order.ShopOrderDiscountCreateEventDefiner;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.delivery.param.TradeTimeItemParam;
import com.niu.shop.service.admin.delivery.vo.ShopStoreInfoVo;
import com.niu.shop.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.shop.service.core.delivery.ICoreExpressService;
import com.niu.shop.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.shop.service.core.delivery.ICoreStoreService;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.StoreListVo;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.order.ICoreOrderDiscountService;
import com.niu.shop.service.core.order.ICoreOrderEventService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.CheckStockParam;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderPayParam;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    @Resource
    ICoreManjianService coreManjianService;

    @Resource
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Resource
    ShopOrderDiscountMapper shopOrderDiscountMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    private ICoreMemberAccountService coreMemberAccountService;

    private static OrderCreateDataVo.Delivery delivery = new OrderCreateDataVo.Delivery();
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
        cache.put(RequestUtils.memberId() + "_member_order_" + vo.getOrderKey(), JSONUtil.parseObj(vo).toString(), 3600);
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
    protected synchronized  void selectTakeAddress(OrderCreateDataVo vo, OrderCalculateParam param) {
        OrderCreateDataVo.Basic basic = vo.getBasic();
        OrderCreateDataVo.Delivery delivery = vo.getDelivery();
        String deliveryType = delivery.getDeliveryType();

        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.REAL.getType())) {
            if (deliveryType.equals(OrderDeliveryTypeEnum.STORE.getType())) {
                if (param.getDelivery().getTakeStoreId() > 0) {
                    ShopStore shopStore = shopStoreMapper.selectById(param.getDelivery().getTakeStoreId());
                    ShopStoreInfoVo shopStoreInfoVo = new ShopStoreInfoVo();
                    shopStoreInfoVo = CollectUtils.build(shopStore, ShopStoreInfoVo.class);
                    shopStoreInfoVo.setTimeWeek(JSONUtil.parseArray(shopStore.getTimeWeek()).toList(String.class));
                    shopStoreInfoVo.setTradeTimeJson(JSONUtil.parseArray(shopStore.getTradeTimeJson()).toList(TradeTimeItemParam.class));
                    delivery.setTakeStore(shopStoreInfoVo);
                }
            } else {
                MemberAddress address = null;
                if (param.getDelivery().getTakeAddressId() > 0) {
                    address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>().eq("id", param.getDelivery().getTakeAddressId()).eq("member_id", param.getMemberId()));
                } else {
                    address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>().eq("is_default", 1).eq("member_id", param.getMemberId()));

                    //如果没有默认地址，将第一个地址设置作为订单地址
                    if(ObjectUtil.isEmpty(address))
                    {
                        address = memberAddressMapper.selectOne(new QueryWrapper<MemberAddress>().eq("member_id", param.getMemberId()).orderByDesc("id").last("LIMIT 1"));
                    }
                }

                if (deliveryType.equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType()) && ObjectUtil.isNotEmpty(address) && address.getLat().isEmpty()) {
                    vo.getError().add("所选的地址没有设置地图定位");
                }

                if (address == null) {
                    delivery.setTakeAddress(List.of());
                } else {
                    delivery.setTakeAddress(address);
                }
            }
        }
        this.delivery = delivery;
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

        this.calculateManjian(vo);
        this.calculateCoupon(vo, param);

        OrderDiscountCalculateEvent event = new OrderDiscountCalculateEvent();
        event.setSiteId(vo.getSiteId());
        event.addAppSign("shop");
        event.setName("OrderDiscountCalculateEvent");
        event.setVo(vo);
        event.setParam(param);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 计算满减
     * @param vo
     */
    protected void calculateManjian(OrderCreateDataVo vo) {
        coreManjianService.calculate(vo);

    }


    /**
     * 计算优惠券优惠
     *
     * @param vo
     * @param param
     */
    protected void calculateCoupon(OrderCreateDataVo vo, OrderCalculateParam param) {
        vo.getBasic().setCouponMoney(BigDecimal.ZERO);
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
                    boolean hasIntersection = goodsCategory.stream().anyMatch(categoryIds::contains);
                    if (hasIntersection) {
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
            } else if (matchGoodsMoney.compareTo(new BigDecimal(0)) == 0 || matchGoodsMoney.compareTo(memberCoupon.getMinConditionMoney()) == -1) {
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
                vo.getBasic().setCouponMoney(vo.getBasic().getCouponMoney().add(discountMoney));

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
            if (vo.getDiscount().get("coupon") != null) {
                vo.getDiscount().remove("coupon");
                vo.getBasic().setCouponMoney(BigDecimal.ZERO);
            }
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

        //扩展增加类似满减

        if(ObjectUtil.isNotEmpty(vo.getDiscountList()))
        {
            List<ShopOrderDiscount> discountListAll = new LinkedList<>();
            for (Map.Entry<String, Map<Integer, OrderCreateDataVo.Discount>> entry : vo.getDiscountList().entrySet()) {

                for (OrderCreateDataVo.Discount item : entry.getValue().values()) {

                    ShopOrderDiscount orderDiscount = new ShopOrderDiscount();
                    BeanUtil.copyProperties(item, orderDiscount);
                    orderDiscount.setOrderId(vo.getOrderId());
                    orderDiscount.setCreateTime(System.currentTimeMillis() / 1000);
                    orderDiscount.setSiteId(vo.getSiteId());
                    discountListAll.add(orderDiscount);
                }

                coreOrderDiscountService.inserAll(discountListAll);
            }
        }
    }

    /**
     * 处理限时折扣
     * @param vo
     */
    private void useDiscountActive(OrderCreateDataVo vo) {
        vo.getGoodsData().forEach((skuId, sku) -> {
            if (sku.getShowType() != null && sku.getShowType().equals(GoodsPriceTypeEnum.DISCOUNT_PRICE.getType())) {
                ShopOrderDiscount model = new ShopOrderDiscount();
                model.setSiteId(vo.getSiteId());
                model.setOrderId(vo.getOrderId());
                model.setMemberId(vo.getMemberId());
                model.setGoodsId(sku.getGoodsId());
                model.setSkuId(sku.getSkuId());
                model.setType("discount");
                model.setNum(sku.getNum());
                model.setMoney(sku.getGoodsMoney());
                model.setDiscountType(ActiveClassEnum.DISCOUNT.getType());
                model.setDiscountTypeId(sku.getActiveId());
                model.setContent("");
                model.setCreateTime(System.currentTimeMillis() / 1000);
                shopOrderDiscountMapper.insert(model);
            }
        });
    }

    protected void addFormData(OrderCreateDataVo vo)
    {
        if(ObjectUtil.isNotEmpty(vo.getFormData()))
        {
            if(vo.getFormData().containsKey("order"))
            {
                JSONObject orderForm = vo.getFormData().getJSONObject("order");
                if(ObjectUtil.isNotEmpty(orderForm))
                {
                    DiyFormRecordsParam orderParam = new DiyFormRecordsParam();
                    orderParam.setSiteId(vo.getSiteId());
                    orderParam.setMemberId(vo.getMemberId());
                    orderParam.setRelateId(vo.getOrderId());
                    orderParam.setFormId(orderForm.getInt("form_id"));
                    orderParam.setValue(orderForm.getJSONArray("value"));
                    ShopOrder shopOrder = shopOrderMapper.selectById(vo.getOrderId());
                    shopOrder.setFormRecordId(coreDiyFormRecordsService.add(orderParam));
                    if (shopOrder.getFormRecordId() != null) shopOrderMapper.updateById(shopOrder);
                }

            }
            if(vo.getFormData().containsKey("goods"))
            {
                JSONObject goodsForm = vo.getFormData().getJSONObject("goods");
                List<ShopOrderGoods> orderGoodsList = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", vo.getOrderId()));
                for (String key : goodsForm.keySet()) {
                    JSONObject goodsFormItem = goodsForm.getJSONObject(key);
                    if(ObjectUtil.isNotEmpty(goodsFormItem))
                    {
                        for (ShopOrderGoods orderGoods : orderGoodsList) {
                            if(key.equals(orderGoods.getSkuId().toString()))
                            {
                                DiyFormRecordsParam goodsParam = new DiyFormRecordsParam();
                                goodsParam.setSiteId(vo.getSiteId());
                                goodsParam.setMemberId(vo.getMemberId());
                                goodsParam.setRelateId(orderGoods.getOrderGoodsId());
                                goodsParam.setFormId(goodsFormItem.getInt("form_id"));
                                goodsParam.setValue(goodsFormItem.getJSONArray("value"));
                                orderGoods.setFormRecordId(coreDiyFormRecordsService.add(goodsParam));
                                if (orderGoods.getFormRecordId() != null) shopOrderGoodsMapper.updateById(orderGoods);
                            }
                        }
                    }


                }
            }
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

        this.addFormData(vo);

        this.useDiscountActive(vo);

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

        if (order.getOrderMoney().compareTo(BigDecimal.ZERO) == 0) {
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

    /**
     * 校验库存
     * @param orderGoodsData
     */
    protected void checkStock(Map<Integer, CheckStockParam> orderGoodsData) {
        for (CheckStockParam param : orderGoodsData.values()) {
            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().select("stock").eq("sku_id", param.getSkuId()));
            if (goodsSku.getStock().compareTo(param.getNum()) == -1) throw new CommonException("商品"+ param.getGoodsName() +"库存不足");
        }
    }
}
