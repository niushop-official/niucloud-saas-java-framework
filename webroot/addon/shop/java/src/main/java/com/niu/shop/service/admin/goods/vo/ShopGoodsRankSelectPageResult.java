package com.niu.shop.service.admin.goods.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShopGoodsRankSelectPageResult extends PageResult<ShopGoodsRankSelectPageInfo> {
    private List<Integer> verifyRankIds;

    public ShopGoodsRankSelectPageResult(Page<?> page, List<ShopGoodsRankSelectPageInfo> list, List<Integer> verifyRankIds) {
        super(page.getCurrent(), page.getSize(), page.getTotal(), list);
        this.verifyRankIds = verifyRankIds;
    }
}
