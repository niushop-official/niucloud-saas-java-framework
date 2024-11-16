package com.niu.shop.service.admin.delivery;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryShippingTemplateParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryShippingTemplateSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryShippingTemplateInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryShippingTemplateListVo;

import java.util.List;

/**
 * 运费模板服务接口
 */
public interface IShopDeliveryShippingTemplateService {


    /**
     * 运费模板分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    PageResult<ShopDeliveryShippingTemplateListVo> page(PageParam pageParam, ShopDeliveryShippingTemplateSearchParam searchParam);

    /**
     * 运费模板列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    List<ShopDeliveryShippingTemplateListVo> list(ShopDeliveryShippingTemplateSearchParam searchParam);

    /**
     * 运费模板详情
     * @param id 主键ID
     * @return ShopDeliveryShippingTemplateInfoVo
     */
    ShopDeliveryShippingTemplateInfoVo info(Integer id);

    /**
     * 运费模板添加
     * @param addParam 添加参数
     */
    void add(ShopDeliveryShippingTemplateParam addParam);

    /**
     * 运费模板编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopDeliveryShippingTemplateParam editParam);

    /**
     * 运费模板删除
     * @param id 主键ID
     */
    void del(Integer id);

}
