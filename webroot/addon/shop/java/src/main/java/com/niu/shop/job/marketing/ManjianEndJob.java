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

@JobProvider(key = "ManjianEndJob", name = "满减活动到时自动关闭", describe = "满减活动到时自动关闭", source = "shop")
@Slf4j
public class ManjianEndJob extends AbstractJobProvider implements IJobProvider {

    private ShopManjianMapper shopManjianMapper() {
        return SpringContext.bean(ShopManjianMapper.class);
    }

    private ShopManjianGoodsMapper shopManjianGoodsMapper() {
        return SpringContext.bean(ShopManjianGoodsMapper.class);
    }
    @Override
    public void exec(JobExecutionContext context) {
        try {
            log.info("满减活动结束开始");
            List<ShopManjian> list = shopManjianMapper().selectList(new QueryWrapper<ShopManjian>()
                    .eq("status", ShopManjianStatusEnum.ACTIVE.getValue())
                    .gt("end_time", 0)
                    .lt("end_time", System.currentTimeMillis() / 1000)
            );
            if (ObjectUtil.isNotEmpty(list)) {
                for (ShopManjian shopManjian : list)
                {
                    shopManjian.setStatus(ShopManjianStatusEnum.END.getValue());
                    shopManjianMapper().updateById(shopManjian);
                    ShopManjianGoods shopManjianGoods = new ShopManjianGoods();
                    shopManjianGoods.setStatus(ShopManjianStatusEnum.END.getValue());
                    shopManjianGoodsMapper().update(shopManjianGoods, new QueryWrapper<ShopManjianGoods>().eq("manjian_id", shopManjian.getManjianId()));
                }
            }
            log.info("满减活动结束结束");
        }catch (Exception e)
        {
            log.info("满减活动结束失败" + e.getMessage());
        }

    }
}
