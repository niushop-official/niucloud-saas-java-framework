package com.niu.shop.service.api.goods.vo;

import com.niu.core.common.domain.PageResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ShopGoodsRankGoodsPageVo extends PageResult<ShopGoodsRankGoodsListInfo> {
    private Integer rankId;
    private String name;

    public ShopGoodsRankGoodsPageVo(PageResult<ShopGoodsRankGoodsListInfo> iPage, Integer rankId, String name) {
        this.rankId = rankId;
        this.name = name;
        this.setData(iPage.getData());
        this.setTotal(iPage.getTotal());
        this.setPerPage(iPage.getPerPage());
        this.setCurrentPage(iPage.getCurrentPage());
    }
}
