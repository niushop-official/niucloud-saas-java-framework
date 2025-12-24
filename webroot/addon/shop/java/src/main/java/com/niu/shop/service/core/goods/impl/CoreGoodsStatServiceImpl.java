package com.niu.shop.service.core.goods.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.pay.PayRefund;
import com.niu.shop.entity.goods.ShopGoodsStat;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.mapper.goods.ShopGoodsStatMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import com.niu.shop.service.core.goods.param.GoodsStatParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreGoodsStatServiceImpl implements ICoreGoodsStatService {
    private final ShopGoodsStatMapper shopGoodsStatMapper;
    private final ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Override
    public void addStat(GoodsAddStatParam param) {
        if (param.getGoodsId() <= 0) {
            return;
        }

        QueryWrapper<ShopGoodsStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date", DateUtils.currInitDate())
                .eq("date_time", DateUtils.StringToTimestamp(new DateTime().toString("yyyy-MM-dd")))
                .eq("site_id", param.getSiteId())
                .eq("goods_id", param.getGoodsId());

        ShopGoodsStat stat = shopGoodsStatMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(stat)) {
            stat = new ShopGoodsStat();
            BeanUtils.copyProperties(param, stat);
            stat.setDate(DateUtils.currInitDate());
            stat.setDateTime(DateUtils.StringToTimestamp(new DateTime().toString("yyyy-MM-dd")));
            stat.setSiteId(param.getSiteId());
            stat.setGoodsId(param.getGoodsId());
            shopGoodsStatMapper.insert(stat);
            return;
        }

        stat.setSaleNum(param.getSaleNum() + stat.getSaleNum());
        stat.setCartNum(param.getCartNum() + stat.getCartNum());
        stat.setPayNum(param.getPayNum() + stat.getPayNum());
        stat.setRefundNum(param.getRefundNum() + stat.getRefundNum());
        stat.setEvaluateNum(param.getEvaluateNum() + stat.getEvaluateNum());
        stat.setGoodsVisitMemberCount(param.getGoodsVisitMemberCount() + stat.getGoodsVisitMemberCount());
        stat.setCollectNum(param.getCollectNum() + stat.getCollectNum());
        stat.setAccessNum(param.getAccessNum() + stat.getAccessNum());
        stat.setPayMoney(param.getPayMoney() == null ? stat.getPayMoney() : param.getPayMoney().add(stat.getPayMoney()));
        stat.setRefundMoney(param.getRefundMoney() == null ? stat.getRefundMoney() : param.getRefundMoney().add(stat.getRefundMoney()));
        shopGoodsStatMapper.updateById(stat);
    }

    @Override
    public void saveGoodsPayNumAndMoneyByOrderId(Integer orderId) {
        QueryWrapper<ShopOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        shopOrderGoodsMapper.selectList(queryWrapper)
                .stream()
                .map(shopOrderGood -> {
                    GoodsAddStatParam param = new GoodsAddStatParam();
                    param.setSiteId(shopOrderGood.getSiteId());
                    param.setGoodsId(shopOrderGood.getGoodsId());
                    param.setPayNum(shopOrderGood.getNum());
                    param.setPayMoney(shopOrderGood.getOrderGoodsMoney());
                    return param;
                })
                .forEach(this::addStat);
    }

    @Override
    public void saveGoodsRefundNumAndMoneyByOrderId(PayRefund refund) {
        QueryWrapper<ShopOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_goods_id", refund.getTradeId());
        shopOrderGoodsMapper.selectList(queryWrapper)
                .stream()
                .map(goods -> {
                    GoodsAddStatParam param = new GoodsAddStatParam();
                    param.setSiteId(goods.getSiteId());
                    param.setGoodsId(goods.getGoodsId());
                    param.setRefundNum(goods.getNum());
                    param.setRefundMoney(goods.getOrderGoodsMoney());
                    return param;
                })
                .forEach(this::addStat);
    }

    @Override
    public void decStat(GoodsStatParam param) {
        String date = DateUtils.timestampToString(param.getTime(), "yyyy-MM-dd");
        Long dateTime = DateUtils.StringToTimestamp(date);
        ShopGoodsStat shopGoodsStat = shopGoodsStatMapper.selectOne(new LambdaQueryWrapper<ShopGoodsStat>()
                .eq(ShopGoodsStat::getDate, date)
                .eq(ShopGoodsStat::getDateTime, dateTime)
                .eq(ShopGoodsStat::getSiteId, param.getSiteId())
                .eq(ShopGoodsStat::getGoodsId, param.getGoodsId()));
        if (shopGoodsStat != null){
            shopGoodsStat.setSaleNum(Math.max(shopGoodsStat.getSaleNum() - param.getSaleNum(), 0));
            shopGoodsStatMapper.updateById(shopGoodsStat);
        }
    }

}
