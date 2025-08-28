package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelListVo;

import java.util.List;

/**
 * 商品标签服务接口
 */
public interface IShopGoodsLabelService {

    /**
     * 商品标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    PageResult<ShopGoodsLabelListVo> page(PageParam pageParam, ShopGoodsLabelSearchParam searchParam);

    /**
     * 商品标签列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    List<ShopGoodsLabelListVo> list(ShopGoodsLabelSearchParam searchParam);

    /**
     * 商品标签详情
     * @param id 主键ID
     * @return ShopGoodsLabelInfoVo
     */
    ShopGoodsLabelInfoVo info(Integer id);

    /**
     * 商品标签添加
     * @param addParam 添加参数
     */
    void add(ShopGoodsLabelParam addParam);

    /**
     * 商品标签编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsLabelParam editParam);

    /**
     * 商品标签删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改商品标签排序
     *
     * @param editParam
     */
    void modifySort(EditGoodsLabelSortParam editParam);

    /**
     *  修改商品标签状态
     * @param editParam
     */
    void modifyStatus(EditGoodsLabelStatusParam editParam);

    /**
     * 复制商品标签
     * @param copyParam 复制参数
     */
    void copy(Integer id);
}
