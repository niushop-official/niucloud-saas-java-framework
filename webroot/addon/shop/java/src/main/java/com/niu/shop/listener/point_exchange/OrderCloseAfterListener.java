package com.niu.shop.listener.point_exchange;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.event.order.OrderCloseAfterEvent;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.core.pointexchange.ICoreGoodsSaleNumService;
import com.niu.shop.service.core.pointexchange.param.ExchangeDataChangeParam;
import com.niu.shop.service.core.pointexchange.vo.OrderCreateDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@EventListen("shop")
@Component
public class OrderCloseAfterListener extends AbstractListener {

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreGoodsSaleNumService coreGoodsSaleNumService;

    public void handleEvent(OrderCloseAfterEvent event) {
        ShopOrder order = event.getOrder();
        if (order.getActivityType().equals("exchange")) {

            List<ShopOrderGoods> orderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()));

            for (ShopOrderGoods item: orderGoods) {
                OrderCreateDataVo.ExchangeInfo extend = JSONUtil.toBean(JSONUtil.parseObj(item.getExtend()), OrderCreateDataVo.ExchangeInfo.class);

                // 返还库存
                coreGoodsSaleNumService.changeStock(order.getRelateId(), item.getSkuId(), item.getNum());

                ExchangeDataChangeParam dec = new ExchangeDataChangeParam();
                dec.setTotalPointNum(extend.getPoint() * item.getNum());
                dec.setTotalPriceNum(item.getGoodsMoney());
                dec.setTotalOrderNum(1);
                dec.setTotalExchangeNum(item.getNum());
                coreGoodsSaleNumService.dec(order.getRelateId(), dec);
            }

            // 返还积分
            coreMemberAccountService.addLog(
                    order.getSiteId(),
                    order.getMemberId(),
                    AccountTypeEnum.POINT.getType(),
                    Double.valueOf(order.getPoint()),
                    order.getPayTime() > 0 ? "account_point_exchange_refund" : "account_point_exchange_close",
                    order.getPayTime() > 0 ? "积分商城订单维权成功积分返还" : "积分商城订单关闭积分返还",
                    order.getOrderId().toString()
            );
        }
    }
}
