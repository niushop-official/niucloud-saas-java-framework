package com.niu.shop.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.cart.ShopCart;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.enums.coupon.CouponTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.shop.mapper.cart.ShopCartMapper;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.mapper.exchange.ShopPointExchangeMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.shop.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.shop.service.core.goods.param.GetShowPriceParam;
import com.niu.shop.service.core.goods.vo.ShowPriceVo;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import com.niu.shop.service.core.order.ICoreOrderCreateService;
import com.niu.shop.service.core.order.helper.OrderCreateHelper;
import com.niu.shop.service.core.order.param.CheckStockParam;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import com.niu.shop.service.core.order.vo.OrderCouponListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoreOrderCreateServiceImpl extends OrderCreateHelper implements ICoreOrderCreateService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    ShopCartMapper cartMapper;

    @Resource
    IGoodsService goodsService;

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;

    @Resource
    ShopCouponGoodsMapper shopCouponGoodsMapper;

    @Resource
    ShopPointExchangeMapper shopPointExchangeMapper;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;

    /**
     * 计算
     *
     * @param param
     */
    public OrderCreateDataVo calculate(OrderCalculateParam param) {
        OrderCreateDataVo vo = null;

        //如果有顺手买一件需要重构
        if (param.getOrderKey().isEmpty() || param.getIsNeedRecalculate() == 1 || CollectionUtils.isNotEmpty(param.getImpulseBuyGoods())) {
            vo = this.confirm(param);
        } else {
            vo = this.loadOrderVo(param.getOrderKey(), OrderCreateDataVo.class);
        }

        vo.getError().clear();

        // 计算运费
        calculateDelivery(vo, param);

        //检测限购
        checkGoodsLimitBuy(vo);
        // 计算优惠
        calculateDiscount(vo, param);

        // 计算订单金额
        OrderCreateDataVo.Basic basic = vo.getBasic();

        if (ObjectUtil.isNotEmpty(vo.getImpulseBuyList())) {
            for (OrderCreateDataVo.Sku impulseBuyGoods : vo.getImpulseBuyList().values()) {
                basic.setGoodsMoney(basic.getGoodsMoney().add(impulseBuyGoods.getGoodsMoney()));
            }
        }

        BigDecimal orderMoney = basic.getGoodsMoney();
        orderMoney = orderMoney.add(basic.getDeliveryMoney());
        orderMoney = orderMoney.subtract(basic.getDiscountMoney());

        basic.setOrderMoney(orderMoney);
        if (basic.getOrderMoney().compareTo(BigDecimal.ZERO) == -1) basic.setOrderMoney(BigDecimal.ZERO);

        cacheOrderVo(vo);

        return vo;
    }

    /**
     * 订单创建
     *
     * @param param
     * @return
     */
    @Override
    public OrderCreateResultVo create(OrderCreateParam param) {
        OrderCreateDataVo vo = loadOrderVo(param.getOrderKey(), OrderCreateDataVo.class);

        // 判断有没有错误信息
        if (vo.getError().size() > 0) throw new CommonException(vo.getError().get(0));

        vo.setOrderType("shop");

        // 检测库存
        createCheckStock(vo);

        vo.setInvoice(param.getInvoice());
        vo.setMemberRemark(param.getMemberRemark());
        vo.setFormData(param.getFormData());

        // 订单
        ShopOrder order = new ShopOrder();
        order.setOrderNo(createOrderNo());
        order.setSiteId(vo.getSiteId());
        order.setOrderFrom(vo.getOrderFrom());
        order.setIp(RequestUtils.ip());
        order.setOrderType("shop");
        order.setStatus(OrderStatusEnum.WAIT_PAY.getStatus().toString());
        order.setBody(vo.getBasic().getBody());
        order.setMemberId(vo.getMemberId());
        order.setGoodsMoney(vo.getBasic().getGoodsMoney());

        order.setDeliveryMoney(vo.getBasic().getDeliveryMoney());
        order.setDiscountMoney(vo.getBasic().getDiscountMoney());
        order.setOrderMoney(vo.getBasic().getOrderMoney());
        order.setHasGoodsTypes(JSONUtil.parseArray(vo.getBasic().getHasGoodsTypes()).toString());
        order.setDeliveryType(vo.getDelivery().getDeliveryType());
        order.setMemberRemark(vo.getMemberRemark());
        order.setTakerName(param.getDelivery().getTakerName());
        order.setTakerMobile(param.getDelivery().getTakerMobile());

        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeAddress())) {
            MemberAddress address = JSONUtil.toBean(JSONUtil.parseObj(vo.getDelivery().getTakeAddress()), MemberAddress.class);
            order.setTakerName(address.getName());
            order.setTakerMobile(address.getMobile());
            order.setTakerProvince(address.getProvinceId());
            order.setTakerCity(address.getCityId());
            order.setTakerDistrict(address.getDistrictId());
            order.setTakerAddress(address.getAddress());
            order.setTakerFullAddress(address.getFullAddress());
            order.setTakerLatitude(address.getLat());
            order.setTakerLongitude(address.getLng());
        }
        if (vo.getDelivery().getDeliveryType().equals("local_delivery") && ObjectUtil.defaultIfNull(param.getDelivery().getLocalDeliveryType(), "").equals("now")) {
            order.setBuyerAskDeliveryTime("");
        } else {
            order.setBuyerAskDeliveryTime(ObjectUtil.defaultIfNull(param.getDelivery().getBuyerAskDeliveryTime(), ""));
        }
        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeStore())) {
            order.setTakeStoreId(vo.getDelivery().getTakeStore().getStoreId());
        }
        order.setRelateId(vo.getExtendData().getInt("relate_id", 0));
        order.setActivityType(vo.getExtendData().getStr("activity_type", ""));
        order.setCreateTime(System.currentTimeMillis() / 1000);

        // 订单项

        List<ShopOrderGoods> orderGoods = new LinkedList<>();

        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            ShopOrderGoods item = new ShopOrderGoods();
            item.setSiteId(vo.getSiteId());
            item.setMemberId(vo.getMemberId());
            item.setSkuId(sku.getSkuId());
            item.setGoodsId(sku.getGoodsId());
            item.setGoodsName(sku.getGoods().getGoodsName());
            item.setGoodsImage(sku.getGoods().getGoodsCover());
            item.setSkuName(sku.getSkuName());
            item.setSkuImage(sku.getSkuImage());
            item.setPrice(sku.getPrice());
            item.setOriginalPrice(sku.getOriginalPrice());
            item.setNum(sku.getNum());
            item.setGoodsMoney(sku.getGoodsMoney());
            item.setGoodsType(sku.getGoods().getGoodsType());
            item.setDiscountMoney(sku.getDiscountMoney().compareTo(sku.getGoodsMoney()) == 1 ? sku.getGoodsMoney() : sku.getDiscountMoney());
            item.setOrderGoodsMoney(sku.getGoodsMoney().subtract(sku.getDiscountMoney()));
            item.setStatus(1);
            item.setIsGift(0);
            item.setExtend(sku.getExtend() != null ? sku.getExtend().toString() : "");
            orderGoods.add(item);
        }

        if (ObjectUtil.isNotEmpty(vo.getImpulseBuyList())) {
            vo.getImpulseBuyList().forEach((Integer key, OrderCreateDataVo.Sku skuItem) -> {
                ShopOrderGoods item = new ShopOrderGoods();
                item.setSiteId(vo.getSiteId());
                item.setMemberId(vo.getMemberId());
                item.setSkuId(skuItem.getSkuId());
                item.setGoodsId(skuItem.getGoodsId());
                item.setGoodsName(skuItem.getGoods().getGoodsName());
                item.setGoodsImage(skuItem.getGoods().getGoodsCover());
                item.setSkuName(skuItem.getSkuName());
                item.setSkuImage(skuItem.getSkuImage());
                item.setPrice(skuItem.getPrice());
                item.setOriginalPrice(skuItem.getOriginalPrice());
                item.setNum(skuItem.getNum());
                item.setGoodsMoney(skuItem.getGoodsMoney());
                item.setGoodsType(skuItem.getGoods().getGoodsType());
                item.setDiscountMoney(skuItem.getDiscountMoney());
                item.setOrderGoodsMoney(skuItem.getGoodsMoney().subtract(skuItem.getDiscountMoney()));
                item.setStatus(1);
                item.setIsGift(0);
                item.setExtend(skuItem.getExtend() != null ? skuItem.getExtend().toString() : "");
                orderGoods.add(item);
            });
        }

        if (ObjectUtil.isNotEmpty(vo.getGiftGoods())) {
            vo.getGiftGoods().forEach((Integer key, OrderCreateDataVo.Sku skuItem) -> {
                ShopOrderGoods item = new ShopOrderGoods();
                item.setSiteId(vo.getSiteId());
                item.setMemberId(vo.getMemberId());
                item.setSkuId(skuItem.getSkuId());
                item.setGoodsId(skuItem.getGoodsId());
                item.setGoodsName(skuItem.getGoods().getGoodsName());
                item.setGoodsImage(skuItem.getGoods().getGoodsCover());
                item.setSkuName(skuItem.getSkuName());
                item.setSkuImage(skuItem.getSkuImage());
                item.setPrice(skuItem.getPrice());
                item.setOriginalPrice(skuItem.getOriginalPrice());
                item.setNum(skuItem.getNum());
                item.setGoodsMoney(skuItem.getGoodsMoney());
                item.setGoodsType(skuItem.getGoods().getGoodsType());
                item.setDiscountMoney(skuItem.getDiscountMoney());
                item.setOrderGoodsMoney(skuItem.getGoodsMoney().subtract(skuItem.getDiscountMoney()));
                item.setStatus(1);
                item.setIsGift(1);
                item.setExtend(skuItem.getExtend() != null ? skuItem.getExtend().toString() : "");
                orderGoods.add(item);
            });
        }

        return this.createOrder(vo, order, orderGoods);
    }

    private void createCheckStock(OrderCreateDataVo vo) {
        Map<Integer, CheckStockParam> skuNumData = new HashMap<>();
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            skuNumData.put(sku.getSkuId(), CheckStockParam
                .builder()
                .goodsId(sku.getGoodsId())
                .skuId(sku.getSkuId())
                .num(sku.getNum())
                .goodsName(sku.getGoods().getGoodsName() + sku.getSkuName())
                .build());
        }
        if (ObjectUtil.isNotEmpty(vo.getGiftGoods())) {
            for (OrderCreateDataVo.Sku sku : vo.getGiftGoods().values()) {
                if (skuNumData.get(sku.getSkuId()) == null) {
                    skuNumData.put(sku.getSkuId(), CheckStockParam
                        .builder()
                        .goodsId(sku.getGoodsId())
                        .skuId(sku.getSkuId())
                        .num(sku.getNum())
                        .goodsName(sku.getGoods().getGoodsName() + sku.getSkuName())
                        .build());
                } else {
                    skuNumData.get(sku.getSkuId()).setNum(skuNumData.get(sku.getSkuId()).getNum() + sku.getNum());
                }
            }
        }
        if (ObjectUtil.isNotEmpty(vo.getImpulseBuyList())) {
            for (OrderCreateDataVo.Sku sku : vo.getImpulseBuyList().values()) {
                if (skuNumData.get(sku.getSkuId()) == null) {
                    skuNumData.put(sku.getSkuId(), CheckStockParam
                        .builder()
                        .goodsId(sku.getGoodsId())
                        .skuId(sku.getSkuId())
                        .num(sku.getNum())
                        .goodsName(sku.getGoods().getGoodsName() + sku.getSkuName())
                        .build());
                } else {
                    skuNumData.get(sku.getSkuId()).setNum(skuNumData.get(sku.getSkuId()).getNum() + sku.getNum());
                }
            }
        }
        checkStock(skuNumData);
    }

    /**
     * 订单确认
     *
     * @param param
     * @return
     */
    private OrderCreateDataVo confirm(OrderCalculateParam param) {
        OrderCreateDataVo vo = new OrderCreateDataVo();
        BeanUtil.copyProperties(param, vo, "discount");

        // 查询会员信息
        OrderCreateDataVo.Buyer buyer = new OrderCreateDataVo.Buyer();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("nickname, point").eq("site_id", param.getSiteId()).eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        BeanUtil.copyProperties(member, buyer);
        buyer.setMemberLevel(memberLevelService.findMemberLevel(param.getMemberId()));
        vo.setBuyer(buyer);
        //查询系统表单信息
        OrderConfigVo orderConfig = coreOrderConfigService.getConfig(param.getSiteId());
        if (ObjectUtil.isNotEmpty(orderConfig.getFormId())) {
            DiyForm diyForm = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().select("form_id").eq("form_id", orderConfig.getFormId()).eq("status", 1));
            if (diyForm != null) vo.setFormId(orderConfig.getFormId());
        }
        // 查询商品信息
        this.getGoodsData(vo, param);

        // 查询配送方式
        this.getDelivery(vo, param);

        return vo;
    }

    /**
     * 检测限购
     *
     * @param vo
     */
    private void checkGoodsLimitBuy(OrderCreateDataVo vo) {
        String res = coreGoodsLimitBuyService.checkGoodsLimitBuy(vo.getMemberId(), vo.getLimitBuy());
        if (!res.equals("")) throw new CommonException(res);
    }

    /**
     * 查询商品信息
     *
     * @param vo
     * @param param
     */
    private void getGoodsData(OrderCreateDataVo vo, OrderCalculateParam param) {
        Map<Integer, OrderCreateDataVo.Sku> goodsData = new HashMap<>();
        OrderCreateDataVo.Basic basic = vo.getBasic();

        if (ObjectUtil.isNotEmpty(param.getCartIds())) {
            List<OrderCalculateParam.SkuItem> skuData = CollectUtils.convert(cartMapper.selectList(new QueryWrapper<ShopCart>()
                    .in("id", param.getCartIds())
                    .select("sku_id, num")
                    .eq("site_id", param.getSiteId())
                    .eq("member_id", param.getMemberId())), OrderCalculateParam.SkuItem.class);
            param.setSkuData(skuData);
        }
        if (ObjectUtil.isNotEmpty(param.getSkuData())) {
            for (OrderCalculateParam.SkuItem item : param.getSkuData()) {
                // 查询规格数据
                OrderCreateDataVo.Sku skuVo = new OrderCreateDataVo.Sku();
                ShopGoodsSku sku = shopGoodsSkuMapper.selectById(item.getSkuId());
                Assert.notNull(sku, "购物车数据已过期");

                String memberPrice = sku.getMemberPrice();
                sku.setMemberPrice(null);

                BeanUtil.copyProperties(sku, skuVo);
                skuVo.setNum(item.getNum());
                skuVo.setOriginalPrice(skuVo.getPrice());

                // 查询商品数据
                OrderCreateDataVo.Goods goodsVo = new OrderCreateDataVo.Goods();
                ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("site_id", param.getSiteId()).eq("goods_id", skuVo.getGoodsId()).eq("status", 1).eq("delete_time", 0));
                Assert.notNull(goods, "购物车数据已过期");
                BeanUtil.copyProperties(goods, goodsVo);
                skuVo.setGoods(goodsVo);
                if (StringUtils.isEmpty(sku.getSkuImage()) && StringUtils.isNotEmpty(goods.getGoodsCover())){
                    skuVo.setSkuImage(goods.getGoodsCover());
                }
                skuVo.setMemberDiscount(goodsVo.getMemberDiscount());
                skuVo.setShowType("original_price");

                //查询商品限购信息
                OrderCreateDataVo.LimitBuy limitBuy = new OrderCreateDataVo.LimitBuy();
                limitBuy.setGoodsId(skuVo.getGoodsId());
                limitBuy.setSiteId(skuVo.getSiteId());
                limitBuy.setGoodsName(skuVo.getGoods().getGoodsName());
                limitBuy.setStock(skuVo.getGoods().getStock());
                limitBuy.setNum(skuVo.getNum());
                limitBuy.setIsLimit(skuVo.getGoods().getIsLimit());
                limitBuy.setLimitType(skuVo.getGoods().getLimitType());
                limitBuy.setMaxBuy(skuVo.getGoods().getMaxBuy());
                limitBuy.setMinBuy(skuVo.getGoods().getMinBuy());
                if (ObjectUtil.isEmpty(vo.getLimitBuy())) {
                    vo.setLimitBuy(new HashMap<>());
                }
                if (vo.getLimitBuy().containsKey("goods_" + skuVo.getGoodsId().toString())) {
                    limitBuy.setNum(limitBuy.getNum() + skuVo.getNum());
                }
                vo.getLimitBuy().put("goods_" + skuVo.getGoodsId().toString(), limitBuy);

                if (ObjectUtil.isNotEmpty(vo.getBuyer().getMemberLevel())) {
                    JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(memberPrice) ? JSONUtil.parseObj(memberPrice) : new JSONObject();
                    skuVo.setMemberPrice(goodsService.calculateMemberPrice(vo.getBuyer().getMemberLevel(), goods.getMemberDiscount(), memberPriceConfig, sku.getPrice()));
                }

                ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                        .skuId(sku.getSkuId())
                        .price(sku.getPrice())
                        .memberDiscount(goods.getMemberDiscount())
                        .memberPrice(memberPrice)
                        .memberId(RequestUtils.memberId())
                        .build());
                skuVo.setShowPrice(skuShowPrice.getShowPrice());
                skuVo.setShowType(skuShowPrice.getShowType());
                skuVo.setPrice(skuShowPrice.getShowPrice());
                skuVo.setActiveId(skuShowPrice.getActiveId());

                // 小计
                skuVo.setGoodsMoney(skuVo.getPrice().multiply(new BigDecimal(item.getNum())));

                if (item.getImpulseBuyGoodsId() == null){
                    GoodsMarketCalculateEventDefiner.GoodsMarketCalculateResult temp = null;
                            // 计算优惠信息
                    GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent event = new GoodsMarketCalculateEventDefiner.GoodsMarketCalculateEvent();
                    event.addAppSign("shop");
                    event.setSiteId(vo.getSiteId());
                    event.setVo(vo);
                    event.setSkuInfo(skuVo);
                    event.setSkuData(param.getSkuData());
                    List<GoodsMarketCalculateEventDefiner.GoodsMarketCalculateResult> marketData = EventAndSubscribeOfPublisher.publishAndCallback(event);
                    if (CollectionUtils.isNotEmpty(marketData)){
                        temp = marketData.stream().filter(calculateResult -> ObjectUtil.isNotEmpty(calculateResult.getSkuInfo())).findFirst().orElse(null);
                    }
                    if (temp != null){
                        skuVo = temp.getSkuInfo();
                        skuVo.setRelateId(temp.getRelateId());
                        if (ObjectUtil.isNotEmpty(vo.getExtendData())){
                            vo.getExtendData().set("relate_id", temp.getRelateId());
                            vo.getExtendData().set("activity_type", temp.getActiveType());
                        }
                    }
                }

                // 累加商品总价
                basic.setGoodsMoney(basic.getGoodsMoney().add(skuVo.getGoodsMoney()));

                goodsData.put(skuVo.getSkuId(), skuVo);

                if (!basic.getHasGoodsTypes().contains(goodsVo.getGoodsType())) {
                    basic.getHasGoodsTypes().add(goodsVo.getGoodsType());
                }
                basic.setTotalNum(basic.getTotalNum() + item.getNum());
                basic.setBody(basic.getBody() + sku.getSkuName() + goods.getGoodsName());
            }
        }
        if (goodsData.size() == 0) throw new CommonException("购物车数据已过期");

        vo.setGoodsData(goodsData);
    }

    /**
     * 查询订单可用优惠券
     *
     * @param orderKey
     */
    @Override
    public List<OrderCouponListVo> getCoupon(String orderKey) {
        OrderCreateDataVo vo = loadOrderVo(orderKey, OrderCreateDataVo.class);

        List<ShopCouponMember> memberCouponList = shopCouponMemberMapper.selectList(new QueryWrapper<ShopCouponMember>()
                .eq("member_id", vo.getMemberId())
                .eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus())
                .gt("expire_time", System.currentTimeMillis() / 1000));

        List<OrderCouponListVo> couponList = new LinkedList<>();

        if (ObjectUtil.isNotEmpty(memberCouponList)) {
            for (ShopCouponMember item : memberCouponList) {
                OrderCouponListVo couponVo = new OrderCouponListVo();
                BeanUtil.copyProperties(item, couponVo);

                couponVo.setIsNormal(true);

                Boolean matchGoods = false;
                BigDecimal matchGoodsMoney = new BigDecimal(0);

                // 通用券
                if (item.getType().equals(CouponTypeEnum.ALL.getType().toString())) {
                    matchGoods = true;
                    matchGoodsMoney = vo.getBasic().getGoodsMoney();
                }
                // 品类券
                if (item.getType().equals(CouponTypeEnum.CATEGORY.getType().toString())) {
                    List<Integer> categoryIds = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>()
                            .eq("coupon_id", item.getCouponId())
                            .select("category_id")).stream().map(i -> i.getCategoryId()).collect(Collectors.toList());

                    for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                        List<Integer> goodsCategory = JSONUtil.parseArray(sku.getGoods().getGoodsCategory()).toList(Integer.class);
                        boolean hasIntersection = goodsCategory.stream().anyMatch(categoryIds::contains);
                        if (hasIntersection) {
                            matchGoods = true;
                            matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney());
                        }
                    }
                }
                // 商品券
                if (item.getType().equals(CouponTypeEnum.GOODS.getType().toString())) {
                    List<Integer> goodsIds = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>()
                            .eq("coupon_id", item.getCouponId())
                            .select("goods_id")).stream().map(i -> i.getGoodsId()).collect(Collectors.toList());

                    for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
                        if (goodsIds.contains(sku.getGoodsId())) {
                            matchGoods = true;
                            matchGoodsMoney = matchGoodsMoney.add(sku.getGoodsMoney());
                        }
                    }
                }

                if (!matchGoods) {
                    couponVo.setIsNormal(false);
                    couponVo.setError("没有适用的商品");
                } else if (matchGoodsMoney.compareTo(BigDecimal.ZERO) == 0 || matchGoodsMoney.compareTo(item.getMinConditionMoney()) == -1) {
                    couponVo.setIsNormal(false);
                    couponVo.setError("未达到最低可使用金额");
                }

                couponList.add(couponVo);
            }
        }
        return couponList;
    }
}
