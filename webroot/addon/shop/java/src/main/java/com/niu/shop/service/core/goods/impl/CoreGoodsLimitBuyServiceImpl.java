package com.niu.shop.service.core.goods.impl;

import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class CoreGoodsLimitBuyServiceImpl implements ICoreGoodsLimitBuyService {

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Override
    public String checkGoodsLimitBuy(Integer memberId, Map<String, OrderCreateDataVo.LimitBuy> goodsList) {
        int goodsCount = goodsList.size();
        for (OrderCreateDataVo.LimitBuy item : goodsList.values()) {
            if (item.getMinBuy() > 0) {
                if (item.getStock() < item.getMinBuy()) {
                    return (goodsCount > 1? "商品“" + item.getGoodsName() + "”" : "该商品") + "库存数量小于起购数量";
                }
                if (item.getNum() < item.getMinBuy()) {
                    return (goodsCount > 1? "商品“" + item.getGoodsName() + "”" : "该商品") + item.getMinBuy() + "件起购";
                }
            }
            if (item.getIsLimit() == 1 && item.getMaxBuy() > 0) {
                if (item.getLimitType() == 1) { // 单次限购
                    if (item.getNum() > item.getMaxBuy()) {
                        return (goodsCount > 1? "商品“" + item.getGoodsName() + "”" : "该商品") + "单次限购" + item.getMaxBuy() + "件";
                    }
                } else if (item.getLimitType() == 2) { // 长期限购
                    int sum = getGoodsHasBuyNumber(item.getSiteId(), memberId, item.getGoodsId());
                    if (sum + item.getNum() > item.getMaxBuy()) {
                        return (goodsCount > 1? "商品“" + item.getGoodsName() + "”" : "该商品") + "每人限购" + item.getMaxBuy() + "件" + (sum > 0? "，您已购买" + sum + "件" : "");
                    }
                }
            }
        }
        return "";
    }

    @Override
    public Integer getGoodsHasBuyNumber(Integer siteId, Integer memberId, Integer goodsId) {

        MPJQueryWrapper<ShopOrderGoods> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("IFNULL(SUM(nsog.num), 0) as sum_num")
                .setAlias("nsog")
                .innerJoin("?_shop_order nso ON nsog.order_id = nso.order_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("nso.site_id", siteId)
                .eq("nsog.member_id", memberId)
                .eq("nsog.goods_id", goodsId)
                .ne("nso.status", OrderStatusEnum.CLOSE.getStatus())
                .ne("nsog.status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus());
        return shopOrderGoodsMapper.selectObjs(queryWrapper).stream()
                .map(o -> new BigDecimal(o.toString()).intValue())
                .findFirst().orElse(0);
    }
}
