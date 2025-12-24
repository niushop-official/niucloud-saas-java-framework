package com.niu.shop.service.admin.marketing.vo;

import com.niu.core.common.domain.PageResult;
import lombok.Data;

import java.util.List;

@Data
public class ShopPointExchangeSelectListVo extends PageResult<ShopPointExchangeListVo> {
    private List<Integer> verifyIds;
}
