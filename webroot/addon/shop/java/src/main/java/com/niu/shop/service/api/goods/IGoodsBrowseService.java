package com.niu.shop.service.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.goods.param.GoodsBrowseDeleteParam;
import com.niu.shop.service.api.goods.param.GoodsBrowseParam;
import com.niu.shop.service.api.goods.param.GoodsBrowseSearchParam;
import com.niu.shop.service.api.goods.vo.GoodsBrowseListVo;

/**
 * 商品浏览历史
 */
public interface IGoodsBrowseService {

    /**
     * 商品浏览历史列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrowseListVo>
     */
    PageResult<GoodsBrowseListVo> page(PageParam pageParam, GoodsBrowseSearchParam searchParam);

    /**
     * 商品浏览历史添加
     * @param addParam 添加参数
     */
    void add(GoodsBrowseParam addParam);

    /**
     * 商品浏览历史删除
     */
    void del(GoodsBrowseDeleteParam param);
}
