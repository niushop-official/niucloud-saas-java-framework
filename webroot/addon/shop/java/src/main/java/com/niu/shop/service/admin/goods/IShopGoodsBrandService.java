package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.EditGoodsBrandSortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsBrandParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsBrandSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsBrandInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsBrandListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 商品品牌服务接口
 */
public interface IShopGoodsBrandService {


    /**
     * 商品品牌列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    PageResult<ShopGoodsBrandListVo> page(PageParam pageParam, ShopGoodsBrandSearchParam searchParam);

    /**
     * 商品品牌列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    List<ShopGoodsBrandListVo> list(ShopGoodsBrandSearchParam searchParam);

    /**
     * 商品品牌详情
     *
     * @param id 主键ID
     * @return ShopGoodsBrandInfoVo
     */
    ShopGoodsBrandInfoVo info(Integer id);

    /**
     * 商品品牌添加
     *
     * @param addParam 添加参数
     */
    void add(ShopGoodsBrandParam addParam);

    /**
     * 商品品牌编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopGoodsBrandParam editParam);

    /**
     * 商品品牌删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改品牌排序
     *
     * @param editParam
     */
    void modifySort(EditGoodsBrandSortParam editParam);
}
