package com.niu.shop.listener.point_exchange;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.event.order.OrderCloseAfterEvent;
import com.niu.shop.event.order.OrderCreateAfterEvent;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.core.pointexchange.ICoreGoodsSaleNumService;
import com.niu.shop.service.core.pointexchange.param.ExchangeDataChangeParam;
import com.niu.shop.service.core.pointexchange.vo.OrderCreateDataVo;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

@EventListen("shop")
@Component
public class OrderCreateAfterListener extends AbstractListener {

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreGoodsSaleNumService coreGoodsSaleNumService;

    public void handleEvent(OrderCreateAfterEvent event) {
        ShopOrder order = event.getOrder();
        if (order.getActivityType().equals("exchange")) {

            List<ShopOrderGoods> orderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()));

            for (ShopOrderGoods item: orderGoods) {
                OrderCreateDataVo.ExchangeInfo extend = JSONUtil.toBean(JSONUtil.parseObj(item.getExtend()), OrderCreateDataVo.ExchangeInfo.class);

                // 减少库存
                coreGoodsSaleNumService.changeStock(order.getRelateId(), item.getSkuId(), - item.getNum());

                ExchangeDataChangeParam inc = new ExchangeDataChangeParam();
                inc.setTotalPointNum(extend.getPoint() * item.getNum());
                inc.setTotalPriceNum(item.getGoodsMoney());
                inc.setTotalOrderNum(1);
                inc.setTotalExchangeNum(item.getNum());
                coreGoodsSaleNumService.inc(order.getRelateId(), inc);
            }

        }
    }
}
