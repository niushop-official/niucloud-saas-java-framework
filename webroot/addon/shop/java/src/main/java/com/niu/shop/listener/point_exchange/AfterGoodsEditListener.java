package com.niu.shop.listener.point_exchange;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.shop.entity.exchange.ShopPointExchange;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.event.goods.AfterGoodsEditEvent;
import com.niu.shop.mapper.exchange.ShopPointExchangeMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.marketing.IShopPointExchangeService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@EventListen("shop")
@Component
public class AfterGoodsEditListener extends AbstractListener {
    @Resource
    private ShopPointExchangeMapper pointExchangeMapper;

    @Resource
    private IShopPointExchangeService shopPointExchangeService;

    @Resource
    private ShopGoodsSkuMapper shopGoodsSkuMapper;

    public void handleEvent(AfterGoodsEditEvent event) {
        log.info("AfterGoodsEditListener 数据:{}", JSONUtil.toJsonStr(event));
        ShopPointExchange shopPointExchangeInfo = pointExchangeMapper.selectOne(new LambdaQueryWrapper<ShopPointExchange>()
                .like(ShopPointExchange::getProductDetail, "\"goods_id\":" + event.getGoodsId())
                .last("LIMIT 1"));
        if (ObjectUtils.isEmpty(shopPointExchangeInfo)){
            return;
        }
        Integer status = event.getGoodsData().getStatus();
        Map<Integer, ShopGoodsSku> skuMap = shopGoodsSkuMapper.selectList(new LambdaQueryWrapper<ShopGoodsSku>()
                        .eq(ShopGoodsSku::getGoodsId, event.getGoodsId()))
                .stream().collect(Collectors.toMap(ShopGoodsSku::getSkuId, Function.identity()));
        shopPointExchangeService.afterGoodsEdit(shopPointExchangeInfo, status, skuMap);
    }
}
