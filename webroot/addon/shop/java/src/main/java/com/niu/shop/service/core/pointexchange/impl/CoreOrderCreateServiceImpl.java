package com.niu.shop.service.core.pointexchange.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.shop.entity.exchange.ShopPointExchange;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.exchange.ShopPointExchangeMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.core.order.helper.OrderCreateHelper;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import com.niu.shop.service.core.pointexchange.ICoreOrderCreateService;
import com.niu.shop.service.core.pointexchange.vo.OrderCreateDataVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CoreOrderCreateServiceImpl extends OrderCreateHelper implements ICoreOrderCreateService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    ShopPointExchangeMapper shopPointExchangeMapper;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Override
    protected <T extends com.niu.shop.service.core.order.vo.OrderCreateDataVo> T loadOrderVo(String orderKey, Class<T> clazz) {
        JSONObject data = loadOrderData(orderKey);
        T vo = JSONUtil.toBean(data, clazz);

        Map<Integer, OrderCreateDataVo.Sku> goodsData = new HashMap<>();
        JSONObject goodsDataJson = JSONUtil.parseObj(data).getJSONObject("goodsData");
        for (String key: goodsDataJson.keySet()) {
            goodsData.put(Integer.valueOf(key), JSONUtil.toBean(goodsDataJson.getJSONObject(key), OrderCreateDataVo.Sku.class));
        }
        vo.setGoodsData(goodsData);

        return vo;
    }

    /**
     * 订单计算
     *
     * @param param
     * @return
     */
    @Override
    public OrderCreateDataVo calculate(OrderCalculateParam param) {
        OrderCreateDataVo vo = null;

        if (param.getOrderKey().isEmpty()) {
            vo = this.confirm(param);
        } else {
            vo = this.loadOrderVo(param.getOrderKey(), OrderCreateDataVo.class);
        }

        vo.getError().clear();

        // 计算运费
        calculateDelivery(vo, param);

        // 计算订单金额
        OrderCreateDataVo.Basic basic = vo.getBasic();
        basic.setOrderMoney(basic.getGoodsMoney().add(basic.getDeliveryMoney()).subtract(basic.getDiscountMoney()));
        if (basic.getOrderMoney().compareTo(BigDecimal.ZERO) == -1) basic.setOrderMoney(BigDecimal.ZERO);

        cacheOrderVo(vo);

        return vo;
    }

    /**
     * 订单确认
     *
     * @param param
     * @return
     */
    private OrderCreateDataVo confirm(OrderCalculateParam param) {
        OrderCreateDataVo vo = new OrderCreateDataVo();
        BeanUtil.copyProperties(param, vo);

        // 查询会员信息
        OrderCreateDataVo.Buyer buyer = new OrderCreateDataVo.Buyer();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("nickname, point").eq("site_id", param.getSiteId()).eq("member_id", param.getMemberId()));
        if (member == null) throw new CommonException("找不到买家");
        BeanUtil.copyProperties(member, buyer);
        buyer.setMemberLevel(memberLevelService.findMemberLevel(param.getMemberId()));
        vo.setBuyer(buyer);

        // 查询商品信息
        this.getGoodsData(vo, param);

        // 查询配送方式
        this.getDelivery(vo, param);

        return vo;
    }

    /**
     * 查询商品信息
     * @param vo
     * @param param
     */
    private void getGoodsData(OrderCreateDataVo vo, OrderCalculateParam param) {
        Map<Integer, OrderCreateDataVo.Sku> goodsData = new HashMap<>();
        OrderCreateDataVo.Basic basic = vo.getBasic();

        Integer skuId = param.getSkuData().get(0).getSkuId();

        ShopPointExchange exchangeGoods = shopPointExchangeMapper.selectOne(new QueryWrapper<ShopPointExchange>()
                .eq("site_id", param.getSiteId())
                .like("product_detail", "\"sku_id\":" + skuId + ",")
        );
        Assert.notNull(exchangeGoods, "活动未找到");
        if (!exchangeGoods.getStatus().equals(1)) throw new CommonException("积分商品已经下架");

        OrderCreateDataVo.ExchangeInfo exchangeInfo = null;
        JSONArray productDetail = JSONUtil.parseArray(exchangeGoods.getProductDetail());
        for (int i = 0; i < productDetail.size(); i++) {
            JSONObject item = productDetail.getJSONObject(i);
            if (item.getInt("sku_id", 0).equals(skuId)) {
                exchangeInfo = JSONUtil.toBean(item, OrderCreateDataVo.ExchangeInfo.class);
            }
        }
        Assert.notNull(exchangeInfo, "活动未找到");

        if (ObjectUtil.isNotEmpty(param.getSkuData())) {
            for (OrderCalculateParam.SkuItem item: param.getSkuData()) {
                // 查询规格数据
                OrderCreateDataVo.Sku skuVo = new OrderCreateDataVo.Sku();
                ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().eq("site_id", param.getSiteId()).eq("sku_id", item.getSkuId()));
                Assert.notNull(sku, "购物车数据已过期");

                String memberPrice = sku.getMemberPrice();
                sku.setMemberPrice(null);

                BeanUtil.copyProperties(sku, skuVo);
                skuVo.setNum(item.getNum());
                skuVo.setOriginalPrice(skuVo.getPrice());
                skuVo.setExchangeId(exchangeGoods.getId());
                skuVo.setExchangeInfo(exchangeInfo);
                skuVo.setPrice(exchangeInfo.getPrice());
                skuVo.setMemberPrice(exchangeInfo.getPrice());
                skuVo.setPointSum(exchangeInfo.getPoint() * item.getNum());

                // 查询商品数据
                OrderCreateDataVo.Goods goodsVo = new OrderCreateDataVo.Goods();
                ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("site_id", param.getSiteId()).eq("goods_id", skuVo.getGoodsId()).eq("status", 1).eq("delete_time", 0));
                Assert.notNull(goods, "购物车数据已过期");
                BeanUtil.copyProperties(goods, goodsVo);
                skuVo.setGoods(goodsVo);

                // 小计
                skuVo.setGoodsMoney(skuVo.getPrice().multiply(new BigDecimal(item.getNum())));

                // 累加商品总价
                basic.setGoodsMoney(basic.getGoodsMoney().add(skuVo.getGoodsMoney()));
                basic.setPointSum(basic.getPointSum() + skuVo.getPointSum());

                goodsData.put(skuVo.getSkuId(), skuVo);

                if (!basic.getHasGoodsTypes().contains(goodsVo.getGoodsType())) {
                    basic.getHasGoodsTypes().add(goodsVo.getGoodsType());
                }
                basic.setTotalNum(basic.getTotalNum() + item.getNum());
                basic.setBody(basic.getBody() + sku.getSkuName() + goods.getGoodsName());

                // 添加优惠项
                OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                discount.setMatchGoodsIds(new Integer[]{ sku.getSkuId() });
                discount.setNum(skuVo.getPointSum());
                discount.setType("discount");
                discount.setMoney(skuVo.getGoodsMoney());
                discount.setDiscountType("exchange");
                discount.setDiscountTypeId(exchangeGoods.getId());
                discount.setTitle(exchangeGoods.getNames());
                discount.setContent("积分兑换");
                vo.getDiscount().put("discount", discount);
            }
        }

        if (goodsData.size() == 0) throw new CommonException("购物车数据已过期");

        vo.setGoodsData(goodsData);
        vo.setExtendData(new JSONObject()
                .put("relate_id", exchangeGoods.getId())
                .put("activity_type", "exchange")
        );
    }

    /**
     * 订单创建
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public OrderCreateResultVo create(OrderCreateParam param) {
        OrderCreateDataVo vo = loadOrderVo(param.getOrderKey(), OrderCreateDataVo.class);

        // 判断有没有错误信息
        if (vo.getError().size() > 0) throw new CommonException(vo.getError().get(0));

        vo.setOrderType("shop");

        vo.setInvoice(param.getInvoice());
        vo.setMemberRemark(param.getMemberRemark());

        // 判断会员积分账户是否足够
        if (vo.getBasic().getPointSum() > vo.getBuyer().getPoint()) throw new CommonException("账户积分不足");

        this.checkGoods(vo);



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
        if (ObjectUtil.defaultIfNull(param.getDelivery().getLocalDeliveryType(), "").equals("now")) {
            order.setBuyerAskDeliveryTime("");
        } else {
            order.setBuyerAskDeliveryTime(ObjectUtil.defaultIfNull(param.getDelivery().getBuyerAskDeliveryTime(), ""));
        }
        if (ObjectUtil.isNotEmpty(vo.getDelivery().getTakeStore())) {
            order.setTakeStoreId(vo.getDelivery().getTakeStore().getStoreId());
        }
        order.setPoint(vo.getBasic().getPointSum());
        order.setRelateId(vo.getExtendData().getInt("relate_id", 0));
        order.setActivityType(vo.getExtendData().getStr("activity_type", ""));

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
            item.setDiscountMoney(sku.getDiscountMoney());
            item.setOrderGoodsMoney(sku.getGoodsMoney().subtract(sku.getDiscountMoney()));
            item.setStatus(1);
            item.setExtend(JSONUtil.parseObj(sku.getExchangeInfo()).toString());
            orderGoods.add(item);
        }

        OrderCreateResultVo result = this.createOrder(vo, order, orderGoods);

        // 扣除积分
        coreMemberAccountService.addLog(
            vo.getSiteId(),
            vo.getMemberId(),
            AccountTypeEnum.POINT.getType(),
            -Double.valueOf(vo.getBasic().getPointSum()),
            "account_point_exchange_order",
            "积分商城消费扣款积分",
            order.getOrderId().toString()
        );

        return result;
    }

    /**
     *
     * @param vo
     */
    private void checkGoods(OrderCreateDataVo vo) {
        // 检测库存
        for (OrderCreateDataVo.Sku sku : vo.getGoodsData().values()) {
            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().select("stock").eq("sku_id", sku.getSkuId()));
            if (goodsSku.getStock().compareTo(sku.getNum()) == -1) throw new CommonException("商品库存不足");

            Integer skuId = sku.getSkuId();

            ShopPointExchange exchangeGoods = shopPointExchangeMapper.selectOne(new QueryWrapper<ShopPointExchange>()
                    .eq("site_id", vo.getSiteId())
                    .like("product_detail", "\"sku_id\":" + skuId + ",")
            );
            Assert.notNull(exchangeGoods, "活动未找到");
            if (!exchangeGoods.getStatus().equals(1)) throw new CommonException("积分商品已经下架");

            OrderCreateDataVo.ExchangeInfo exchangeInfo = null;
            JSONArray productDetail = JSONUtil.parseArray(exchangeGoods.getProductDetail());
            for (int i = 0; i < productDetail.size(); i++) {
                JSONObject item = productDetail.getJSONObject(i);
                if (item.getInt("sku_id", 0).equals(skuId)) {
                    exchangeInfo = JSONUtil.toBean(item, OrderCreateDataVo.ExchangeInfo.class);
                }
            }
            Assert.notNull(exchangeInfo, "活动未找到");

            if (sku.getNum() > exchangeInfo.getStock()) throw new CommonException("兑换库存不足");
            if (sku.getNum() > exchangeInfo.getLimitNum()) throw new CommonException("超出单次限购数量");
        }
    }
}
