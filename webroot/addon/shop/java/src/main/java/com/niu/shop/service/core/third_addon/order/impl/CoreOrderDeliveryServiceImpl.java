package com.niu.shop.service.core.third_addon.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.core.order.helper.OrderCreateHelper;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.third_addon.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.third_addon.order.param.CalculateDeliveryMoneyParam;
import com.niu.shop.service.core.third_addon.order.vo.ShopGoodsSkuInfoVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class CoreOrderDeliveryServiceImpl extends OrderCreateHelper implements ICoreOrderDeliveryService {
    @Resource
    private MemberAddressMapper memberAddressMapper;

    @Resource
    private ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Override
    public BigDecimal calculate(CalculateDeliveryMoneyParam params) {
        OrderCalculateParam param = setParams(params);
        OrderCreateDataVo vo = new OrderCreateDataVo();
        vo.setSiteId(param.getSiteId());
        vo.setMemberId(param.getMemberId());

        getGoodsData(vo, param);

        OrderCreateDataVo.Basic basic = vo.getBasic();
        if (basic.getHasGoodsTypes().contains(GoodsTypeEnum.VIRTUAL.getType())) {
            vo.getDelivery().setDeliveryType(OrderDeliveryTypeEnum.VIRTUAL.getType());
        } else {
            vo.getDelivery().setDeliveryType("express");
        }
        getDelivery(vo, param);
        calculateDelivery(vo, param);

        return basic.getDeliveryMoney();
    }

    private OrderCalculateParam setParams(CalculateDeliveryMoneyParam params) {
        OrderCalculateParam param = new OrderCalculateParam();

        param.setSiteId(params.getSiteId());
        param.setMemberId(params.getMemberId());

        List<OrderCalculateParam.SkuItem> skuData = new ArrayList<>();
        OrderCalculateParam.SkuItem skuItem = new OrderCalculateParam.SkuItem();
        skuItem.setSkuId(params.getSourceSkuId());
        skuItem.setNum(params.getNum());
        skuData.add(skuItem);
        param.setSkuData(skuData);

        OrderCalculateParam.Delivery delivery = new OrderCalculateParam.Delivery();

        if (params.getProvinceId() > 0 || params.getCityId() > 0 || params.getDistrictId() > 0) {
            MemberAddress address = memberAddressMapper.selectOne(new LambdaQueryWrapper<MemberAddress>()
                    .eq(MemberAddress::getMemberId, params.getMemberId())
                    .eq(MemberAddress::getProvinceId, params.getProvinceId())
                    .eq(MemberAddress::getCityId, params.getCityId())
                    .eq(MemberAddress::getDistrictId, params.getDistrictId())
                    .last("limit 1"));
            delivery.setTakeAddressId(address.getId());
            delivery.setDeliveryType(OrderDeliveryTypeEnum.EXPRESS.getType());
        }

        param.setDelivery(delivery);

        return param;
    }

    private void getGoodsData(OrderCreateDataVo vo, OrderCalculateParam param) {
        List<OrderCalculateParam.SkuItem> skuData = param.getSkuData();
        if (skuData == null || skuData.isEmpty()) {
            throw new CommonException("此商品已下架");
        }

        List<Integer> skuIds = new ArrayList<>();
        for (OrderCalculateParam.SkuItem item : skuData) {
            skuIds.add(item.getSkuId());
        }

        // 查询SKU列表
        List<ShopGoodsSku> goodsSkuList = shopGoodsSkuMapper.selectList(new LambdaQueryWrapper<ShopGoodsSku>()
                .in(ShopGoodsSku::getSkuId, skuIds).eq(ShopGoodsSku::getSiteId, param.getSiteId()));
        List<ShopGoodsSkuInfoVo> skuList = CollectUtils.convert(goodsSkuList, ShopGoodsSkuInfoVo.class);

        Map<Integer, ShopGoodsSkuInfoVo> skuMap = new HashMap<>();
        for (ShopGoodsSkuInfoVo sku : skuList) {
            ShopGoods shopGoods = shopGoodsMapper.selectById(sku.getGoodsId());
            sku.setGoods(shopGoods);
            skuMap.put(sku.getSkuId(), sku);
        }

        // 商品数据
        Map<Integer, OrderCreateDataVo.Sku> goodsData = new HashMap<>();
        BigDecimal goodsMoney = BigDecimal.ZERO;
        int totalNum = 0;
        StringBuilder body = new StringBuilder();
        List<String> hasGoodsTypes = new ArrayList<>();

        for (OrderCalculateParam.SkuItem item : skuData) {
            Integer skuId = item.getSkuId();
            Integer num = item.getNum();
            totalNum += num;

            ShopGoodsSkuInfoVo skuInfo = skuMap.get(skuId);
            if (skuInfo == null || skuInfo.getGoods() == null) {
                throw new CommonException("此商品已下架");
            }

            OrderCreateDataVo.Sku skuVo = new OrderCreateDataVo.Sku();
            BeanUtils.copyProperties(skuInfo, skuVo);
            skuVo.setSkuId(skuId);
            skuVo.setNum(num);

            skuVo.setOriginalPrice(skuInfo.getPrice());

            String goodsType = skuInfo.getGoods().getGoodsType();
            if (!hasGoodsTypes.contains(goodsType)) {
                hasGoodsTypes.add(goodsType);
            }

            BigDecimal price = skuInfo.getPrice();
            BigDecimal itemGoodsMoney = price.multiply(new BigDecimal(num));
            skuVo.setGoodsMoney(itemGoodsMoney);
            goodsMoney = goodsMoney.add(itemGoodsMoney);

            if (body.length() > 0) {
                body.append(skuInfo.getSkuName()).append(skuInfo.getGoods().getGoodsName());
            } else {
                body.append(skuInfo.getSkuName()).append(skuInfo.getGoods().getGoodsName());
            }

            OrderCreateDataVo.Goods goodsVo = new OrderCreateDataVo.Goods();
            BeanUtils.copyProperties(skuInfo.getGoods(), goodsVo);
            skuVo.setGoods(goodsVo);
            goodsData.put(skuId, skuVo);
        }

        OrderCreateDataVo.Basic basic = vo.getBasic();
        basic.setHasGoodsTypes(hasGoodsTypes);
        basic.setTotalNum(totalNum);
        basic.setGoodsMoney(goodsMoney);
        basic.setBody(body.toString());

        vo.setGoodsData(goodsData);
    }


}
