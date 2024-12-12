package com.niu.shop.service.api.marketing.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ShopNewComerGoodsPageVo<T> extends PageResult<T> {
    private long validityTime;
    private long isJoin;

    public ShopNewComerGoodsPageVo(Page<?> page, List<T> list) {
        super(page.getCurrent(), page.getSize(), page.getTotal(), list);
    }
}
