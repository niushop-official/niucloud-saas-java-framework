package com.niu.shop.service.admin.goods;

import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.ShopGoodsInitVo;
import com.niu.shop.service.admin.goods.vo.ShopVirtualGoodsInitVo;

/**
 * 虚拟商品接口
 */
public interface IShopVirtualGoodsService {

    /**
     * 商品添加
     *
     * @param addParam 添加参数
     */
    void add(ShopVirtualGoodsParam addParam);

    /**
     * 商品编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopVirtualGoodsParam editParam);

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    ShopVirtualGoodsInitVo getInit(Integer goodsId);
}
