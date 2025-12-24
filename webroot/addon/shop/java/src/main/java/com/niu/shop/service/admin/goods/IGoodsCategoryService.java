package com.niu.shop.service.admin.goods;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.shop.service.admin.goods.param.EditGoodsCategorySortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCategoryParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCategorySearchParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCategoryInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCategoryListVo;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface IGoodsCategoryService {

    /**
     * 商品分类列表
     * @return List<ShopGoodsCategoryListVo>
     */
    List<ShopGoodsCategoryListVo> list(ShopGoodsCategorySearchParam searchParam);

    /**
     * 获取商品分类树
     *
     * @return
     */
    JSONArray tree();

    /**
     * 获取商品分类树
     *
     * @return
     */
    JSONArray tree(ShopGoodsCategorySearchParam searchParam);

    /**
     * 商品分类详情
     *
     * @param id 主键ID
     * @return ShopGoodsCategoryInfoVo
     */
    ShopGoodsCategoryInfoVo info(Integer id);

    /**
     * 商品分类添加
     *
     * @param addParam 添加参数
     */
    void add(ShopGoodsCategoryParam addParam);

    /**
     * 商品分类编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsCategoryParam editParam);

    /**
     * 分类调整使用
     * @param categoryId
     * @param oldPid
     * @param newPid
     */
    void batchUpdateCategory(Integer categoryId, Integer oldPid, Integer newPid);

    /**
     * 修改分类排序
     *
     * @param editParam
     */
    void editGoodsCategorySort(EditGoodsCategorySortParam editParam);

    /**
     * 商品分类删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    GoodsCategoryConfigVo getGoodsCategoryConfig();

    /**
     * 设置商品分类配置
     *
     * @param data
     */
    void setGoodsCategoryConfig(JSONObject data);
}
