package com.niu.shop.service.api.cart;

import cn.hutool.json.JSONObject;
import com.niu.shop.service.api.cart.param.ShopCartCalculateParam;
import com.niu.shop.service.api.cart.param.ShopCartDeleteParam;
import com.niu.shop.service.api.cart.param.ShopCartParam;
import com.niu.shop.service.api.cart.vo.ShopCartCalculateResVo;
import com.niu.shop.service.api.cart.vo.ShopCartCalculateVo;
import com.niu.shop.service.api.cart.vo.ShopCartGoodsListVo;
import com.niu.shop.service.api.cart.vo.ShopCartListVo;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface IShopCartService {


    /**
     * 购物车列表
     * @return PageResult<ShopCartListVo>
     */
    List<ShopCartListVo> list();

    /**
     * 购物车商品列表
     * @return PageResult<ShopCartListVo>
     */
    List<ShopCartGoodsListVo> goodsList();

    /**
     * 购物车计算
     * @param calculateParam
     * @return
     */
    ShopCartCalculateResVo calculate(ShopCartCalculateParam calculateParam);

    /**
     * 购物车计算获取商品
     * @param calculateParam
     * @return
     */
    ShopCartCalculateVo getGoods(ShopCartCalculateParam calculateParam);


    /**
     * 购物车添加
     * @param addParam 添加参数
     */
    void add(ShopCartParam addParam);

    /**
     * 购物车编辑
     * @param editParam 编辑参数
     */
    void edit(ShopCartParam editParam);

    /**
     * 购物车删除
     * @param param 主键ID
     */
    void del(ShopCartDeleteParam param);

    /**
     * 清空购物车
     */
    void clear();

    /**
     * 获取购物车数量
     *
     * @param goodsId
     * @return
     */
    Integer getSum(Integer goodsId);


}
