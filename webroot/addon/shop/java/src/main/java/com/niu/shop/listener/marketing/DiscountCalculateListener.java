package com.niu.shop.listener.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.SpringContext;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@EventCallback("shop")
@Component
public class DiscountCalculateListener extends GoodsMarketCalculateEventDefiner {

    @Resource
    ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Override
    public GoodsMarketCalculateResult handleCallback(GoodsMarketCalculateEvent event) {
        OrderCreateDataVo vo = event.getVo();
        OrderCreateDataVo.Sku skuInfo = event.getSkuInfo();

        if (skuInfo.getGoods().getIsDiscount().equals(1) && ObjectUtil.isNotEmpty(vo.getExtendData()) && vo.getExtendData().getStr("activity_type", "").equals("discount")) {
            ShopActiveGoods discountGoods = shopActiveGoodsMapper.selectOne(new QueryWrapper<ShopActiveGoods>()
                    .eq("goods_id", skuInfo.getGoodsId())
                    .eq("active_class", ActiveClassEnum.DISCOUNT.getType())
                    .eq("active_goods_status", ActiveStatusEnum.ACTIVE.getStatus())
                    .last("limit 1")
            );

            if (ObjectUtil.isNotEmpty(discountGoods) && skuInfo.getSalePrice().compareTo(skuInfo.getOriginalPrice()) != 0) {
                OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                discount.setMatchGoodsIds(new Integer[]{ skuInfo.getSkuId() });
                discount.setNum(skuInfo.getNum());
                discount.setType("discount");
                discount.setMoney(skuInfo.getPrice().subtract(skuInfo.getSalePrice()));
                discount.setDiscountType("discount");
                discount.setDiscountTypeId(discountGoods.getActiveId());
                discount.setTitle("限时折扣");
                discount.setContent("");
                vo.getDiscount().put("discount_"  + discountGoods.getActiveId(), discount);
            }

            skuInfo.setPrice(skuInfo.getSalePrice());
            skuInfo.setGoodsMoney(skuInfo.getPrice().multiply(new BigDecimal(skuInfo.getNum())));

        }
        return null;
    }
}
