package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrListVo;

import java.util.List;

/**
 * 商品参数服务接口
 */
public interface IShopGoodsAttrService {

    /**
     * 商品参数分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsAttrListVo>
     */
    PageResult<ShopGoodsAttrListVo> page(PageParam pageParam, ShopGoodsAttrSearchParam searchParam);

    /**
     * 商品参数列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsAttrListVo>
     */
    List<ShopGoodsAttrListVo> list(ShopGoodsAttrSearchParam searchParam);

    /**
     * 商品参数详情
     *
     * @param id 主键ID
     * @return ShopGoodsAttrInfoVo
     */
    ShopGoodsAttrInfoVo info(Integer id);

    /**
     * 商品参数添加
     *
     * @param addParam 添加参数
     */
    void add(ShopGoodsAttrParam addParam);

    /**
     * 商品参数编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsAttrParam editParam);

    /**
     * 商品参数删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改商品参数排序号
     *
     * @param editParam
     */
    void modifySort(EditGoodsAttrSortParam editParam);

    /**
     * 修改商品参数名称
     *
     * @param editParam
     */
    void modifyAttrName(EditGoodsAttrNameParam editParam);


    /**
     * 修改商品参数值
     *
     * @param editParam
     */
    void modifyAttrValueFormat(EditGoodsAttrValueParam editParam);
}
