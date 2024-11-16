package com.niu.shop.service.admin.shop_address;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.shop_address.param.ShopAddressParam;
import com.niu.shop.service.admin.shop_address.param.ShopAddressSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.shop_address.vo.ShopAddressInfoVo;
import com.niu.shop.service.admin.shop_address.vo.ShopAddressListVo;

import java.util.List;

/**
 * 商家地址库服务接口
 */
public interface IShopAddressService {


    /**
     * 商家地址库列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopAddressListVo>
     */
    PageResult<ShopAddressListVo> page(PageParam pageParam, ShopAddressSearchParam searchParam);

    /**
     * 商家地址库详情
     *
     * @param id 主键ID
     * @return ShopAddressInfoVo
     */
    ShopAddressInfoVo info(Integer id);

    /**
     * 获取默认发货地址
     *
     * @return ShopAddressInfoVo
     */
    ShopAddressInfoVo defaultDelivery();

    /**
     * 商家地址库添加
     *
     * @param addParam 添加参数
     */
    void add(ShopAddressParam addParam);

    /**
     * 商家地址库编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopAddressParam editParam);

    /**
     * 商家地址库删除
     *
     * @param id 主键ID
     */
    void del(Integer id);


    /**
     * 退款地址列表
     *
     * @return
     */
    List<ShopAddressListVo> refundAddress();

}
