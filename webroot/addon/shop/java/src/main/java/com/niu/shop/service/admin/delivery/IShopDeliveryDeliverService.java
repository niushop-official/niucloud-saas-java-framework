package com.niu.shop.service.admin.delivery;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverListVo;

/**
 * 配送员服务接口
 */
public interface IShopDeliveryDeliverService {


    /**
     * 配送员列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryDeliverListVo>
     */
    PageResult<ShopDeliveryDeliverListVo> list(PageParam pageParam, ShopDeliveryDeliverSearchParam searchParam);

    /**
     * 配送员详情
     * @param id 主键ID
     * @return ShopDeliveryDeliverInfoVo
     */
    ShopDeliveryDeliverInfoVo info(Integer id);

    /**
     * 配送员添加
     * @param addParam 添加参数
     */
    void add(ShopDeliveryDeliverParam addParam);

    /**
     * 配送员编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopDeliveryDeliverParam editParam);

    /**
     * 配送员删除
     * @param id 主键ID
     */
    void del(Integer id);

}
