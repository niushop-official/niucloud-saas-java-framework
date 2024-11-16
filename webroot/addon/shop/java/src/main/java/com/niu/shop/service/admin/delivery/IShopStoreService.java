package com.niu.shop.service.admin.delivery;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopStoreParam;
import com.niu.shop.service.admin.delivery.param.ShopStoreSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.delivery.vo.ShopStoreInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopStoreListVo;

import java.util.List;

/**
 * 自提门店服务接口
 */
public interface IShopStoreService {


    /**
     * 自提门店列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopStoreListVo>
     */
    PageResult<ShopStoreListVo> page(PageParam pageParam, ShopStoreSearchParam searchParam);

    /**
     * 自提门店列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopStoreListVo>
     */
    List<ShopStoreListVo> list(ShopStoreSearchParam searchParam);

    /**
     * 自提门店详情
     * @param id 主键ID
     * @return ShopStoreInfoVo
     */
    ShopStoreInfoVo info(Integer id);

    /**
     * 自提门店添加
     * @param addParam 添加参数
     */
    void add(ShopStoreParam addParam);

    /**
     * 自提门店编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopStoreParam editParam);

    /**
     * 自提门店删除
     * @param id 主键ID
     */
    void del(Integer id);

}
