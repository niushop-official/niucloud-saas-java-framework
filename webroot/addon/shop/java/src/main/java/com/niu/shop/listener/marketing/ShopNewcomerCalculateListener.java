package com.niu.shop.listener.marketing;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@EventCallback("shop")
public class ShopNewcomerCalculateListener extends GoodsMarketCalculateEventDefiner {
    private ShopActiveMapper shopActiveMapper;
    private ShopActiveGoodsMapper shopActiveGoodsMapper;
    private ICoreShopNewcomerService coreShopNewcomerService;

    @Override
    public GoodsMarketCalculateResult handleCallback(GoodsMarketCalculateEvent event) {
        log.debug("参与新人专享活动，ShopNewcomerCalculate: {}", JSON.toJSONString(event));

        OrderCreateDataVo.Sku skuInfo = event.getSkuInfo();
        OrderCreateDataVo orderObj = event.getVo();

        boolean isNewcomer = coreShopNewcomerService.checkIfNewcomer(orderObj.getSiteId(), orderObj.getMemberId());

        if (!isNewcomer || ObjectUtil.hasEmpty(orderObj.getExtendData(), skuInfo) ||
                !orderObj.getExtendData().getStr("activity_type", "").equals(ActiveClassEnum.NEWCOMER_DISCOUNT.getType())) {
            return null;
        }

        LambdaQueryWrapper<ShopActiveGoods> shopActiveGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopActiveGoodsLambdaQueryWrapper.eq(ShopActiveGoods::getGoodsId, skuInfo.getGoodsId())
                .eq(ShopActiveGoods::getSkuId, skuInfo.getSkuId())
                .eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
        ShopActiveGoods activeGoods = shopActiveGoodsMapper.selectOne(shopActiveGoodsLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(activeGoods)) {
            return null;
        }

        ShopActive shopActive = shopActiveMapper.selectById(activeGoods.getActiveId());
        if (!shopActive.getActiveStatus().equals(ActiveStatusEnum.ACTIVE.getStatus())) {
            return null;
        }

        BigDecimal newcomerPrice = JSON.parseObject(activeGoods.getActiveGoodsValue()).getBigDecimal("newcomer_price");
        skuInfo.setNewcomerPrice(newcomerPrice);
        skuInfo.setIsNewcomer(1);
        JSONObject extend = new JSONObject();
        extend.set("is_newcomer", 1)
                .set("newcomer_price", newcomerPrice);
        skuInfo.setExtend(extend);

        OrderCreateDataVo.Discount discount = discountFormat(ListUtil.of(skuInfo.getSkuId()), skuInfo.getMemberPrice(),
                activeGoods.getActiveId(), shopActive.getActiveDesc());
        orderObj.getDiscount().put(ActiveClassEnum.NEWCOMER_DISCOUNT.getType(), discount);

        if (skuInfo.getNum() == 1) {
            skuInfo.setPrice(newcomerPrice);
            skuInfo.setGoodsMoney(skuInfo.getPrice().multiply(BigDecimal.valueOf(skuInfo.getNum())));
        } else {
            BigDecimal memberPrice = skuInfo.getMemberPrice() == null ? skuInfo.getSalePrice() : skuInfo.getMemberPrice();
            BigDecimal goodsMoney = newcomerPrice.add(memberPrice.multiply(new BigDecimal(skuInfo.getNum() - 1)));
            skuInfo.setGoodsMoney(goodsMoney);
        }

        GoodsMarketCalculateResult result = new GoodsMarketCalculateResult();
        result.setSkuInfo(skuInfo);
        return result;
    }

    private OrderCreateDataVo.Discount discountFormat(List<Integer> goodsIds, BigDecimal money, Integer discountTypeId, String activeDesc) {
        OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
        discount.setMatchGoodsIds(goodsIds.toArray(new Integer[]{}));
        discount.setType("discount");
        discount.setNum(1);
        discount.setMoney(money);
        discount.setDiscountType(ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
        discount.setDiscountTypeId(discountTypeId);
        discount.setContent("新人专享");
        discount.setTitle(activeDesc);
        return discount;
    }

    @Autowired
    public void setCoreShopNewcomerService(ICoreShopNewcomerService coreShopNewcomerService) {
        this.coreShopNewcomerService = coreShopNewcomerService;
    }

    @Autowired
    public void setShopActiveGoodsMapper(ShopActiveGoodsMapper shopActiveGoodsMapper) {
        this.shopActiveGoodsMapper = shopActiveGoodsMapper;
    }

    @Autowired
    public void setShopActiveMapper(ShopActiveMapper shopActiveMapper) {
        this.shopActiveMapper = shopActiveMapper;
    }
}
