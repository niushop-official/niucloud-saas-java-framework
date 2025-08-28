package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.*;

import java.util.List;

/**
 * 商品服务接口
 */
public interface IShopGoodsService {


    /**
     * 商品列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    PageResult<ShopGoodsListVo> page(PageParam pageParam, ShopGoodsSearchParam searchParam);

    /**
     * 商品详情
     *
     * @param id 主键ID
     * @return ShopGoodsInfoVo
     */
    ShopGoodsInfoVo info(Integer id);

    /**
     * 商品添加
     *
     * @param addParam 添加参数
     */
    void add(ShopGoodsParam addParam);

    /**
     * 商品编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsParam editParam);

    /**
     * 商品删除
     *
     * @param param
     */
    void del(GetGoodsIdsParam param);

    /**
     * 恢复商品
     *
     * @param param
     */
    void recycle(GetGoodsIdsParam param);

    /**
     * 商品回收站
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    PageResult<ShopGoodsListVo> recyclePage(PageParam pageParam, ShopGoodsSearchParam searchParam);

    /**
     * 修改商品上下架状态
     *
     * @param param
     */
    void editStatus(EditGoodsStatusParam param);

    /**
     * 修改商品上下架状态(单商品）
     *
     * @param param
     */
    void editSimpleStatus(EditGoodsSimpleStatusParam param);

    /**
     * 修改商品排序号
     *
     * @param param
     */
    void editSort(EditGoodsSortParam param);

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    ShopGoodsInitVo getInit(Integer goodsId);

    /**
     * 查询商品参与营销活动的数量
     *
     * @param goodsId
     * @return
     */
    Long getActiveGoodsCount(Integer goodsId);

    /**
     * 查询商品参与营销活动的数量
     *
     * @param goodsIds
     * @return
     */
    Long getActiveGoodsCount(Integer[] goodsIds);

    /**
     * 查询商品正在交易中的数量
     *
     * @param skuIds
     * @return
     */
    Long getSkuTransactionCount(Integer[] skuIds);

    /**
     * 查询商品正在交易中的数量
     *
     * @param goodsId
     * @return
     */
    Long getGoodsTransactionCount(Integer goodsId);

    /**
     * 复制商品
     *
     * @param goodsId
     */
    void copy(Integer goodsId);

    /**
     * 商品选择分页列表
     *
     * @param pageParam
     * @param searchParam
     */
    ShopSelectGoodsListVo getSelectPage(PageParam pageParam, ShopGoodsSelectSearchParam searchParam);

    /**
     * 商品选择分页列表为种草社区
     *
     * @param pageParam
     * @param searchParam
     */
    ShopSelectGoodsListVo getZhongcaoSelectPage(PageParam pageParam, ShopGoodsSelectSearchParam searchParam);


    /**
     * 获取商品选择列表带ku
     */
    List<ShopSelectGoodsListVo.ListVo> getSelectGoodsSku(ShopGoodsSelectSearchParam searchParam);

    /**
     * 查询商品SKU规格列表
     *
     * @param goodsId
     * @return
     */
    List<ShopGoodsSkuListVo> getSkuList(Integer goodsId);

    /**
     * 编辑商品规格列表库存
     *
     * @param param
     */
    void editGoodsListStock(EditGoodsListStockParam param);

    /**
     * 编辑商品规格列表价格
     *
     * @param param
     */
    void editGoodsListPrice(EditGoodsListPriceParam param);

    /**
     * 编辑商品规格列表会员价
     *
     * @param param
     */
    void editGoodsListMemberPrice(EditGoodsListMemberPriceParam param);

    PageResult<GetBuyGoodsSelectVo> getBuyGoodsSelect(PageParam pageParam, BuyGoodsSelectParam param);

    GetBuySkuSelectVo getBuySkuSelect(BuySkuSelectParam param);

    List<GetBuyGoodsSelectedVo> getBuyGoodsSelected(BuyGoodsSelectedParam param);

    /**
     * 批量设置商品
     * @param param
     */
    void batchSet(ShopGoodsBatchSetParam param);

    Boolean verifySkuNo(ShopGoodsVerifySkuNoParam param);
}
