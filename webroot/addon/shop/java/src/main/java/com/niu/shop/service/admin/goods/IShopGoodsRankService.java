package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.ShopGoodsRankInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsRankListVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsRankOptionData;
import com.niu.shop.service.admin.goods.vo.ShopGoodsRankSelectPageResult;
import com.niu.shop.service.core.goods.param.ShopGoodsRankConfigParam;
import com.niu.shop.service.core.goods.vo.ShopGoodsRankConfigVo;

/**
 * 商品排行榜服务接口
 */
public interface IShopGoodsRankService {

    /**
     * 商品排行榜列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsRankListVo>
     */
    PageResult<ShopGoodsRankListVo> page(PageParam pageParam, ShopGoodsRankSearchParam searchParam);

    /**
     * 商品排行榜详情
     *
     * @param id 主键ID
     * @return ShopGoodsRankInfoVo
     */
    ShopGoodsRankInfoVo info(Integer id);

    /**
     * 商品排行榜添加
     *
     * @param addParam 添加参数
     */
    void add(ShopGoodsRankParam addParam);

    /**
     * 商品排行榜编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsRankParam editParam);

    ShopGoodsRankOptionData getOptionData();

    /**
     * 商品排行榜删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    void setGoodsRankConfig(ShopGoodsRankConfigParam param);

    ShopGoodsRankConfigVo getGoodsRankConfig();

    void editSort(ShopGoodsRankEditSortParam param);

    void batchDelete(ShopGoodsRankBatchDeleteParam param);

    ShopGoodsRankSelectPageResult getSelectPage(PageParam pageParam, ShopGoodsRankSelectPageParam param);

    void modifyStatus(ShopGoodsRankModifyStatusParam param);
}
