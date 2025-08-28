package com.niu.shop.job.marketing;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.shop.entity.discount.ShopDiscount;
import com.niu.shop.entity.discount.ShopDiscountGoods;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.mapper.discount.ShopDiscountGoodsMapper;
import com.niu.shop.mapper.discount.ShopDiscountMapper;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@JobProvider(key = "DiscountStartJob", name = "限时折扣自动开启", describe = "限时折扣自动开启", source = "shop")
public class DiscountStartJob extends AbstractJobProvider implements IJobProvider {
    private ShopDiscountMapper shopDiscountMapper() {
        return SpringContext.bean(ShopDiscountMapper.class);
    }

    private ShopDiscountGoodsMapper shopDiscountgoodsMapper() {
        return SpringContext.bean(ShopDiscountGoodsMapper.class);
    }


    private IShopDiscountService shopDiscountService() {
        return SpringContext.bean(IShopDiscountService.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        log.info("限时折扣自动开启");
        try {
            List<ShopDiscount> shopDiscounts = shopDiscountMapper().selectList(new LambdaQueryWrapper<ShopDiscount>()
                    .eq(ShopDiscount::getStatus, ActiveStatusEnum.NOT_ACTIVE.getStatus())
                    .le(ShopDiscount::getStartTime, DateUtils.currTime()));

            if (CollectionUtils.isNotEmpty(shopDiscounts)) {
                List<Integer> ids = shopDiscounts.stream().map(ShopDiscount::getDiscountId).collect(Collectors.toList());

                // 更新折扣活动状态
                shopDiscountMapper().update(null, new LambdaUpdateWrapper<ShopDiscount>()
                        .in(ShopDiscount::getDiscountId, ids)
                        .eq(ShopDiscount::getStatus, ActiveStatusEnum.NOT_ACTIVE.getStatus())
                        .le(ShopDiscount::getStartTime, DateUtils.currTime())
                        .set(ShopDiscount::getStatus, ActiveStatusEnum.ACTIVE.getStatus()));

                // 更新折扣商品状态
                shopDiscountgoodsMapper().update(null, new LambdaUpdateWrapper<ShopDiscountGoods>()
                        .in(ShopDiscountGoods::getDiscountId, ids)
                        .set(ShopDiscountGoods::getStatus, ActiveStatusEnum.ACTIVE.getStatus()));

                // 执行后续操作
                for (Integer id : ids) {
                    shopDiscountService().discountStartAfter(id);
                }
            }
        } catch (Exception e) {
            log.error("限时折扣自动开启error: {}", e.getMessage(), e);
        }
    }
}
