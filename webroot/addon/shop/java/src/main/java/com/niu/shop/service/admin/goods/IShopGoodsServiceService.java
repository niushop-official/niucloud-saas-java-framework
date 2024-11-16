package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.ShopGoodsServiceParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsServiceSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceListVo;

import java.util.List;

/**
 * 商品服务服务接口
 */
public interface IShopGoodsServiceService {

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    PageResult<ShopGoodsServiceListVo> page(PageParam pageParam, ShopGoodsServiceSearchParam searchParam);


    /**
     * 商品服务列表
     *
     * @param searchParam 搜索参数
     * @return List<ShopGoodsServiceListVo>
     */
    List<ShopGoodsServiceListVo> list(ShopGoodsServiceSearchParam searchParam);

    /**
     * 商品服务详情
     * @param id 主键ID
     * @return ShopGoodsServiceInfoVo
     */
    ShopGoodsServiceInfoVo info(Integer id);

    /**
     * 商品服务添加
     * @param addParam 添加参数
     */
    void add(ShopGoodsServiceParam addParam);

    /**
     * 商品服务编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsServiceParam editParam);

    /**
     * 商品服务删除
     * @param id 主键ID
     */
    void del(Integer id);

}
