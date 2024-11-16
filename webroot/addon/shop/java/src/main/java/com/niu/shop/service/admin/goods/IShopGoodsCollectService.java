package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.ShopGoodsCollectParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCollectSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCollectInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCollectListVo;

/**
 * 商品收藏记录服务接口
 */
public interface IShopGoodsCollectService {


    /**
     * 商品收藏记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsCollectListVo>
     */
    PageResult<ShopGoodsCollectListVo> list(PageParam pageParam, ShopGoodsCollectSearchParam searchParam);

    /**
     * 商品收藏记录详情
     * @param id 主键ID
     * @return ShopGoodsCollectInfoVo
     */
    ShopGoodsCollectInfoVo info(Integer id);

    /**
     * 商品收藏记录添加
     * @param addParam 添加参数
     */
    void add(ShopGoodsCollectParam addParam);

    /**
     * 商品收藏记录编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsCollectParam editParam);

    /**
     * 商品收藏记录删除
     * @param id 主键ID
     */
    void del(Integer id);

}
