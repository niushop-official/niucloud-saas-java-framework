package com.niu.shop.service.admin.delivery;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryCompanyParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryCompanySearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryCompanyInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryCompanyListVo;

import java.util.List;

/**
 * 物流公司服务接口
 */
public interface IShopDeliveryCompanyService {

    /**
     * 物流公司分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryCompanyListVo>
     */
    PageResult<ShopDeliveryCompanyListVo> page(PageParam pageParam, ShopDeliveryCompanySearchParam searchParam);

    /**
     * 物流公司列表
     * @param searchParam
     * @return
     */
    List<ShopDeliveryCompanyListVo> list(ShopDeliveryCompanySearchParam searchParam);

    /**
     * 详情
     * @param id 主键ID
     * @return ShopDeliveryCompanyInfoVo
     */
    ShopDeliveryCompanyInfoVo info(Integer id);

    /**
     * 物流公司添加
     * @param addParam 添加参数
     */
    void add(ShopDeliveryCompanyParam addParam);

    /**
     * 物流公司编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopDeliveryCompanyParam editParam);

    /**
     * 物流公司删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 批量添加物流公司
     *
     * @param list
     */
    void insertAll(List<ShopDeliveryCompany> list);
}
