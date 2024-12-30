package com.niu.shop.job.marketing;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.manjian.ShopManjianGoods;
import com.niu.shop.enums.marketing.ShopManjianStatusEnum;
import com.niu.shop.mapper.manjian.ShopManjianGoodsMapper;
import com.niu.shop.mapper.manjian.ShopManjianMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

import java.util.List;
import java.util.stream.Collectors;

@JobProvider(key = "ManjianStartJob", name = "满减活动到时自动开启", describe = "满减活动到时自动开启", source = "shop")
@Slf4j
public class ManjianStartJob extends AbstractJobProvider implements IJobProvider {

    private ShopManjianMapper shopManjianMapper() {
        return SpringContext.bean(ShopManjianMapper.class);
    }

    private ShopManjianGoodsMapper shopManjianGoodsMapper() {
        return SpringContext.bean(ShopManjianGoodsMapper.class);
    }
    @Override
    public void exec(JobExecutionContext context) {
        try{
            log.info("满减活动开启开始");
            List<ShopManjian> list = shopManjianMapper().selectList(new QueryWrapper<ShopManjian>()
                    .eq("status", ShopManjianStatusEnum.NOT_ACTIVE.getValue())
                    .gt("start_time", 0)
                    .lt("start_time", System.currentTimeMillis() / 1000)
            );
            if (ObjectUtil.isNotEmpty(list)) {
                for (ShopManjian shopManjian : list)
                {
                    shopManjian.setStatus(ShopManjianStatusEnum.ACTIVE.getValue());
                    shopManjianMapper().updateById(shopManjian);
                    ShopManjianGoods shopManjianGoods = new ShopManjianGoods();
                    shopManjianGoods.setStatus(ShopManjianStatusEnum.ACTIVE.getValue());
                    shopManjianGoodsMapper().update(shopManjianGoods, new QueryWrapper<ShopManjianGoods>().eq("manjian_id", shopManjian.getManjianId()));
                }
            }
            log.info("满减活动开启完成");
        }catch (Exception e)
        {
            log.info("满减活动开启失败" + e.getMessage());
        }

    }
}
