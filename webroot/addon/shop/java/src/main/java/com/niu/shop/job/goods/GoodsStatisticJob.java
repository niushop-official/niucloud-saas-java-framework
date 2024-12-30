package com.niu.shop.job.goods;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.shop.service.admin.goods.IShopGoodsStatService;
import org.quartz.JobExecutionContext;

@SuppressWarnings("unused")
@JobProvider(key = "GoodsStatisticJob", name = "商品统计更新", describe = "商品统计更新", source = "shop")
public class GoodsStatisticJob extends AbstractJobProvider implements IJobProvider {
    private volatile boolean init = false;
    private IShopGoodsStatService shopGoodsStatService;

    @Override
    public void exec(JobExecutionContext context) {
        if (!init) {
            init();
        }

        shopGoodsStatService.syncStatGoods();
    }

    private void init() {
        this.shopGoodsStatService = SpringContext.bean(IShopGoodsStatService.class);
        init = true;
    }

}
