package com.niu.shop.service.admin.stat.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderRefundStatusEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.service.admin.stat.IShopStatService;
import com.niu.shop.service.admin.stat.vo.GoodsStatVo;
import com.niu.shop.service.admin.stat.vo.OrderStatVo;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.vo.StatDataVo;
import com.niu.shop.service.core.shop.vo.StatVo;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.Arrays;


/**
 * 【请填写功能名称】实现
 */
@Service
public class ShopStatServiceImpl implements IShopStatService {
        
    @Resource
    ICoreStatService coreStatService;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    /**
     * 查询统计总和
     *
     * @return
     */
    @Override
    public StatVo getStat() {
        return coreStatService.getStat(RequestUtils.siteId());
    }

    /**
     * 查询统计总和
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatVo getStat(Long startTime, Long endtTime) {
        return coreStatService.getStat(RequestUtils.siteId(), startTime, endtTime);
    }

    /**
     * 获取时间区间内天统计数据
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatDataVo getStatData(Long startTime, Long endtTime) {
        return coreStatService.getStatData(RequestUtils.siteId(), startTime, endtTime);
    }

    /**
     * 查询订单统计
     *
     * @return
     */
    @Override
    public OrderStatVo orderStat() {
        Integer siteId = RequestUtils.siteId();
        OrderStatVo vo = new OrderStatVo();

        vo.setWaitPayOrder(ObjectUtil.defaultIfNull(shopOrderMapper.selectCount(new QueryWrapper<ShopOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_PAY.getStatus())
        ), 0L));

        vo.setWaitDeliveryOrder(ObjectUtil.defaultIfNull(shopOrderMapper.selectCount(new QueryWrapper<ShopOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus())
        ), 0L));

        vo.setWaitTakeOrder(ObjectUtil.defaultIfNull(shopOrderMapper.selectCount(new QueryWrapper<ShopOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_TAKE.getStatus())
        ), 0L));

        vo.setRefundOrder(ObjectUtil.defaultIfNull(shopOrderGoodsMapper.selectCount(new QueryWrapper<ShopOrderGoods>()
                .eq("site_id", siteId)
                .eq("status", OrderGoodsRefundEnum.REFUNDING.getStatus())
        ), 0L));

        return vo;
    }

    /**
     * 查询商品统计
     *
     * @return
     */
    @Override
    public GoodsStatVo goodsStat() {
        Integer siteId = RequestUtils.siteId();
        GoodsStatVo vo = new GoodsStatVo();

        vo.setSaleGoodsNum(ObjectUtil.defaultIfNull(shopGoodsMapper.selectCount(new QueryWrapper<ShopGoods>()
                .eq("site_id", siteId)
                .eq("status", 1)
        ), 0L));

        vo.setWarehouseGoodsNum(ObjectUtil.defaultIfNull(shopGoodsMapper.selectCount(new QueryWrapper<ShopGoods>()
                .eq("site_id", siteId)
                .eq("status", 0)
        ), 0L));

        return vo;
    }
}
