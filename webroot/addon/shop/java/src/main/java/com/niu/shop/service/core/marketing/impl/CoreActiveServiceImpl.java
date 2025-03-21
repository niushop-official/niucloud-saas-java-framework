package com.niu.shop.service.core.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.event.marketing.ActiveEndAfterEvent;
import com.niu.shop.event.marketing.ActiveStartAfterEvent;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.service.core.marketing.ICoreActiveGoodsService;
import com.niu.shop.service.core.marketing.ICoreActiveService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class CoreActiveServiceImpl implements ICoreActiveService {

    @Resource
    private ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Resource
    private ShopActiveMapper shopActiveMapper;

    @Resource
    private ICoreActiveGoodsService coreActiveGoodsService;

    @Override
    public Integer add(ShopActive shopActive, List<ShopActiveGoods> shopActiveGoodsList) {

        shopActiveMapper.insert(shopActive);

        if (ObjectUtil.isNotEmpty(shopActiveGoodsList)) {
            for (ShopActiveGoods shopActiveGoods : shopActiveGoodsList) {
                shopActiveGoods.setActiveId(shopActive.getActiveId());
            }
            coreActiveGoodsService.insertAll(shopActiveGoodsList);
        }

        //活动开始时间小于当前时间
        if (ObjectUtil.isNotEmpty(shopActive.getStartTime()) && shopActive.getStartTime() != 0 && shopActive.getStartTime() < System.currentTimeMillis() / 1000) {
            this.start(shopActive.getActiveId());
        }
        //活动结束时间大于当前时间
        if (ObjectUtil.isNotEmpty(shopActive.getEndTime()) && shopActive.getEndTime() != 0 && shopActive.getEndTime() <= System.currentTimeMillis() / 1000) {
            this.end(shopActive.getActiveId());
        }

        return shopActive.getActiveId();
    }

    @Override
    public void edit(ShopActive shopActive, List<ShopActiveGoods> shopActiveGoodsList) {

        if (ObjectUtil.isNotEmpty(shopActive.getEndTime()) && shopActive.getEndTime() != 0 && shopActive.getEndTime() < System.currentTimeMillis() / 1000)
            throw new CommonException("活动已结束，无法编辑");

        shopActiveMapper.updateById(shopActive);

        if (ObjectUtil.isNotEmpty(shopActiveGoodsList)) {

            List<ShopActiveGoods> activeGoodsList = shopActiveGoodsMapper.selectList(new QueryWrapper<ShopActiveGoods>().eq("active_id", shopActive.getActiveId()));

            if (ObjectUtil.isNotEmpty(activeGoodsList)) {

                for (ShopActiveGoods activeGoods : activeGoodsList) {
                    for (ShopActiveGoods goods : shopActiveGoodsList) {

                        if (activeGoods.getGoodsId().equals(goods.getGoodsId())) {
                            activeGoods.setActiveGoodsOrderMoney(goods.getActiveGoodsOrderMoney());
                            activeGoods.setActiveGoodsOrderNum(goods.getActiveGoodsOrderNum());
                            activeGoods.setActiveGoodsMemberNum(goods.getActiveGoodsMemberNum());
                            activeGoods.setActiveGoodsSuccessNum(goods.getActiveGoodsSuccessNum());
                        }
                    }
                }

                shopActiveGoodsMapper.delete(new QueryWrapper<ShopActiveGoods>().eq("active_id", shopActive.getActiveId()));
            }

            coreActiveGoodsService.insertAll(shopActiveGoodsList);

            //活动开始时间小于当前时间
            if (ObjectUtil.isNotEmpty(shopActive.getStartTime()) && shopActive.getStartTime() != 0 && shopActive.getStartTime() < System.currentTimeMillis() / 1000) {
                this.start(shopActive.getActiveId());
            }
            //活动结束时间大于当前时间
            if (ObjectUtil.isNotEmpty(shopActive.getEndTime()) && shopActive.getEndTime() != 0 && shopActive.getEndTime() <= System.currentTimeMillis() / 1000) {
                this.end(shopActive.getActiveId());
            }
        }
    }

    @Override
    public void del(Integer activeId, Integer siteId, Integer isForce) {

        ShopActive shopActive = shopActiveMapper.selectOne(new QueryWrapper<ShopActive>().eq("active_id", activeId).eq("site_id", siteId));
        if (ObjectUtil.isEmpty(shopActive)) throw new CommonException("活动不存在");

        if (isForce == 0 && shopActive.getActiveStatus().equals(ActiveStatusEnum.ACTIVE.getStatus()))
            throw new CommonException("活动正在进行中，无法删除");
        shopActiveMapper.deleteById(shopActive.getActiveId());
        shopActiveGoodsMapper.delete(new QueryWrapper<ShopActiveGoods>().eq("active_id", shopActive.getActiveId()));
    }

    @Override
    public void close(Integer activeId, Integer siteId) {

        ShopActive shopActive = new ShopActive();
        shopActive.setActiveStatus(ActiveStatusEnum.CLOSE.getStatus());
        shopActiveMapper.update(shopActive, new QueryWrapper<ShopActive>().eq("active_id", activeId).eq("site_id", siteId).eq("active_status", ActiveStatusEnum.ACTIVE.getStatus()));

        ShopActiveGoods shopActiveGoods = new ShopActiveGoods();
        shopActiveGoods.setActiveGoodsStatus(ActiveStatusEnum.CLOSE.getStatus());
        shopActiveGoodsMapper.update(shopActiveGoods, new QueryWrapper<ShopActiveGoods>().eq("active_id", activeId).eq("site_id", siteId));
    }

    @Override
    public void start(Integer activeId) {

        ShopActive shopActive = new ShopActive();
        shopActive.setActiveStatus(ActiveStatusEnum.ACTIVE.getStatus());
        shopActiveMapper.update(shopActive, new QueryWrapper<ShopActive>().eq("active_id", activeId).eq("active_status", ActiveStatusEnum.NOT_ACTIVE.getStatus()).le("start_time", System.currentTimeMillis() / 1000));

        ShopActiveGoods shopActiveGoods = new ShopActiveGoods();
        shopActiveGoods.setActiveGoodsStatus(ActiveStatusEnum.ACTIVE.getStatus());
        shopActiveGoodsMapper.update(shopActiveGoods, new QueryWrapper<ShopActiveGoods>().eq("active_id", activeId));

        // 活动开启之后事件
        ShopActive active = shopActiveMapper.selectById(activeId);
        ActiveStartAfterEvent event = new ActiveStartAfterEvent();
        event.setSiteId(active.getSiteId());
        event.addAppSign("shop");
        event.setName("ActiveStartAfterEvent");
        event.setActive(active);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    public void end(Integer activeId) {

        ShopActive shopActive = new ShopActive();
        shopActive.setActiveStatus(ActiveStatusEnum.END.getStatus());
        shopActiveMapper.update(shopActive, new QueryWrapper<ShopActive>().eq("active_id", activeId).eq("active_status", ActiveStatusEnum.ACTIVE.getStatus()).le("end_time", System.currentTimeMillis() / 1000));

        ShopActiveGoods shopActiveGoods = new ShopActiveGoods();
        shopActiveGoods.setActiveGoodsStatus(ActiveStatusEnum.END.getStatus());
        shopActiveGoodsMapper.update(shopActiveGoods, new QueryWrapper<ShopActiveGoods>().eq("active_id", activeId));

        // 活动开启之后事件
        ShopActive active = shopActiveMapper.selectById(activeId);
        ActiveEndAfterEvent event = new ActiveEndAfterEvent();
        event.setSiteId(active.getSiteId());
        event.addAppSign("shop");
        event.setName("ActiveEndAfterEvent");
        event.setActive(active);
        EventAndSubscribeOfPublisher.publishAll(event);
    }
}
