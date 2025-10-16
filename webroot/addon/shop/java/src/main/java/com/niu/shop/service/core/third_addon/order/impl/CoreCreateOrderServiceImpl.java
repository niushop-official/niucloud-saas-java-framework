package com.niu.shop.service.core.third_addon.order.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderCreateService;
import com.niu.shop.service.core.order.ICoreOrderEventService;
import com.niu.shop.service.core.order.helper.OrderCreateHelper;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderPayParam;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.third_addon.order.ICoreCreateOrderService;
import com.niu.shop.service.core.third_addon.order.param.CoreThirdPintuanDataParam;
import com.niu.shop.service.core.third_addon.order.vo.CoreOrderCreateResultVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CoreCreateOrderServiceImpl extends OrderCreateHelper implements ICoreCreateOrderService {



        @Resource
        private ShopOrderMapper orderMapper;
        @Resource
        private ShopOrderGoodsMapper orderGoodsMapper;
        @Resource
        private ShopGoodsMapper goodsMapper;
        @Resource
        private ShopGoodsSkuMapper goodsSkuMapper;
        @Resource
        private ICoreOrderEventService coreOrderEventService;
        @Resource
        private ICoreOrderCreateService coreOrderCreateService;

        /**
         * 拼团系统对接订单
         */
        @Override
        public List<CoreOrderCreateResultVo> pintuanCreateOrder(Map<String, Object> data) {
            Integer siteId = (Integer) data.get("siteId");
            Integer sourceId = (Integer) data.get("sourceId");
            String source = (String) data.get("source");

            List<Object> orderData = (List<Object>) data.get("orderData");

            List<CoreThirdPintuanDataParam> relateOrderList = orderData.stream()
                .map(obj -> JSONUtil.toBean(JSONUtil.toJsonStr(obj), CoreThirdPintuanDataParam.class))
                .collect(Collectors.toList());

            List<CoreOrderCreateResultVo> resultList = new ArrayList<>();

            log.info("SHOP_PINTUAN - pintuanCreateOrder 开始处理拼团订单，站点ID: {}，来源ID: {}，总订单数: {}",
                    siteId, sourceId, relateOrderList.size());

            if (CollectionUtils.isEmpty(relateOrderList)) {
                log.warn("SHOP_PINTUAN - pintuanCreateOrder 接收到的订单数据为空");
                return List.of();
            }

            int processedOrders = 0;
            List<String> failedOrders = new ArrayList<>();
            for (int i = 0; i < relateOrderList.size(); i++) {
                CoreThirdPintuanDataParam relateOrderInfo = relateOrderList.get(i);
                int orderIndex = i + 1;
                Integer relateOrderId = relateOrderInfo.getId();
                Integer shopOrderId = null;

                try {
                    if (!"shop".equals(relateOrderInfo.getSource())) {
                        log.warn("SHOP_PINTUAN - 订单数据商品来源非商城，来源: {}", relateOrderInfo.getSource());
                        continue;
                    }

                    JSONObject skuInfo = relateOrderInfo.getSku();
                    Integer goodsId = skuInfo.getInt("sourceId");
                    Integer skuId = skuInfo.getInt("sourceSkuId");
                    Integer num = relateOrderInfo.getNum();

                    if (!checkGoods(siteId, goodsId, skuId, num)) {
                        CoreOrderCreateResultVo result = new CoreOrderCreateResultVo();
                        result.setOrderId(relateOrderId);
                        result.setRelateOrderId(0);
                        result.setRelateOrderItemId(0);
                        result.setIsSuccess(false);
                        result.setReason("商品不存在或库存不足");
                        resultList.add(result);
                        continue;
                    }

                    if (goodsId == null || skuId == null) {
                        failedOrders.add("第" + orderIndex + "个订单缺少商品或SKU信息");
                        continue;
                    }

                    // 获取商品类型
                    ShopGoods goods = goodsMapper.selectOne(new LambdaQueryWrapper<ShopGoods>()
                            .eq(ShopGoods::getSiteId, siteId)
                            .eq(ShopGoods::getGoodsId, goodsId)
                            .select(ShopGoods::getGoodsType));

                    String goodsType = goods != null ? goods.getGoodsType() : "";
                    String thirdType = OrderDeliveryTypeEnum.VIRTUAL.getType().equals(goodsType) ?
                            OrderDeliveryTypeEnum.VIRTUAL.getType() : OrderDeliveryTypeEnum.EXPRESS.getType();

                    // 构建订单数据
                    ShopOrder order = new ShopOrder();
                    BeanUtils.copyProperties(relateOrderInfo, order);
                    order.setSiteId(siteId);
                    order.setOrderType("shop");
                    order.setStatus(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString());
                    order.setDeliveryMoney(BigDecimal.ZERO);
                    order.setDiscountMoney(BigDecimal.ZERO);
                    order.setHasGoodsTypes(goodsType);
                    order.setDeliveryType(thirdType);
                    order.setTakeStoreId(0);
                    order.setMemberRemark("");
                    order.setOrderNo(createOrderNo());
                    order.setIsEnableRefund(1);
                    order.setInvoiceId(0);
                    order.setRelateId(sourceId);
                    order.setRelateOrderId(relateOrderId);
                    order.setRelateSource(source);
                    order.setActivityType(source);

                    // 检查订单是否已存在
                    ShopOrder existingOrder = orderMapper.selectOne(new LambdaQueryWrapper<ShopOrder>()
                            .eq(ShopOrder::getSiteId, siteId)
                            .eq(ShopOrder::getRelateId, sourceId)
                            .eq(ShopOrder::getRelateSource, source)
                            .eq(ShopOrder::getRelateOrderId, relateOrderId));

                    if (existingOrder != null) {
                        shopOrderId = existingOrder.getOrderId();
                        log.info("SHOP_PINTUAN - 订单已存在 订单ID: {}", shopOrderId);
                    } else {
                        orderMapper.insert(order);
                        shopOrderId = order.getOrderId();
                        log.info("SHOP_PINTUAN - 创建订单成功 订单ID: {}", shopOrderId);
                    }

                    if (shopOrderId == null) {
                        failedOrders.add("第" + orderIndex + "个订单创建失败");
                        continue;
                    }

                    // 构建订单商品数据
                    JSONObject goodsInfo = skuInfo.getJSONObject("goods");
                    BigDecimal goodsSingleMoney = relateOrderInfo.getGoodsMoney().divide(
                            BigDecimal.valueOf(relateOrderInfo.getNum()), 2, RoundingMode.HALF_UP);

                    ShopOrderGoods orderGoods = new ShopOrderGoods();
                    orderGoods.setSiteId(siteId);
                    orderGoods.setMemberId(relateOrderInfo.getMemberId());
                    orderGoods.setGoodsId(goodsId);
                    orderGoods.setSkuId(skuId);
                    orderGoods.setGoodsName(goodsInfo.getStr("title"));
                    orderGoods.setSkuName(skuInfo.getStr("skuName"));
                    orderGoods.setGoodsImage(goodsInfo.getStr("cover"));
                    orderGoods.setSkuImage(skuInfo.getStr("skuImage"));
                    orderGoods.setPrice(goodsSingleMoney);
                    orderGoods.setOriginalPrice(skuInfo.getJSONObject("priceConfig").getBigDecimal("original"));
                    orderGoods.setNum(relateOrderInfo.getNum());
                    orderGoods.setGoodsMoney(relateOrderInfo.getGoodsMoney());
                    orderGoods.setGoodsType(goodsType);
                    orderGoods.setOrderGoodsMoney(goodsSingleMoney);
                    orderGoods.setOrderId(shopOrderId);
                    orderGoods.setDiscountMoney(BigDecimal.ZERO);
                    orderGoods.setStatus(1);
                    orderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
                    orderGoods.setExtend("");
                    orderGoods.setIsGift(0);
                    orderGoods.setIsEnableRefund(1);

                    orderGoodsMapper.insert(orderGoods);
                    log.info("SHOP_PINTUAN - 创建订单商品成功 订单商品ID: {}", orderGoods.getOrderGoodsId());

                    // 返回成功结果
                    CoreOrderCreateResultVo result = new CoreOrderCreateResultVo();
                    result.setOrderId(relateOrderId);
                    result.setRelateOrderId(shopOrderId);
                    result.setRelateOrderItemId(orderGoods.getOrderGoodsId());
                    result.setIsSuccess(true);
                    resultList.add(result);

                    // 触发订单事件
                    triggerOrderEvents(siteId, order, orderGoods, relateOrderInfo);

                    processedOrders++;

                } catch (Exception e) {
                    failedOrders.add("第" + orderIndex + "个订单处理异常: " + e.getMessage());
                    log.error("SHOP_PINTUAN - 拼团订单处理失败: ", e);
                }
            }

            log.info("SHOP_PINTUAN - 拼团订单处理完成，总订单数: {}，成功处理: {}，失败: {}",
                    relateOrderList.size(), processedOrders, failedOrders.size());

            return resultList;
        }

        private boolean checkGoods(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
            ShopGoods goods = goodsMapper.selectOne(new LambdaQueryWrapper<ShopGoods>()
                    .eq(ShopGoods::getSiteId, siteId)
                    .eq(ShopGoods::getGoodsId, goodsId));
            if (goods == null) return false;

            ShopGoodsSku sku = goodsSkuMapper.selectOne(new LambdaQueryWrapper<ShopGoodsSku>()
                    .eq(ShopGoodsSku::getSiteId, siteId)
                    .eq(ShopGoodsSku::getGoodsId, goodsId)
                    .eq(ShopGoodsSku::getSkuId, skuId));

            return sku != null && sku.getStock() >= num;
        }

        private void triggerOrderEvents(Integer siteId, ShopOrder order, ShopOrderGoods orderGoods, CoreThirdPintuanDataParam relateOrderInfo) {
//           TODO 订单事件触发需要看下需要哪些参数
            OrderCalculateParam calculateParam = new OrderCalculateParam();
            calculateParam.setSkuData(new LinkedList<>());
            OrderCalculateParam.SkuItem skuItem = new OrderCalculateParam.SkuItem();
            skuItem.setNum(orderGoods.getNum());
            skuItem.setSkuId(orderGoods.getSkuId());
            calculateParam.getSkuData().add(skuItem);

            OrderCreateDataVo createDataVo = coreOrderCreateService.calculate(calculateParam);
            coreOrderEventService.orderCreateAfter(createDataVo);

            OrderPayParam payParam = new OrderPayParam();
            payParam.setOutTradeNo(order.getOutTradeNo());
            payParam.setTradeId(order.getOrderId());
            payParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
            payParam.setMainId(order.getMemberId());
            coreOrderEventService.orderPayAfter(order, payParam);

            log.info("SHOP_PINTUAN - 订单事件触发完成");
        }

}
