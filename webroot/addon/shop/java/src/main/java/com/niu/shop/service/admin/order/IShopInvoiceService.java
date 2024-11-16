package com.niu.shop.service.admin.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopInvoiceParam;
import com.niu.shop.service.admin.order.param.ShopInvoiceSearchParam;
import com.niu.shop.service.admin.order.vo.ShopInvoiceInfoVo;
import com.niu.shop.service.admin.order.vo.ShopInvoiceListVo;

/**
 * 发票服务接口
 */
public interface IShopInvoiceService {


    /**
     * 发票列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopInvoiceListVo>
     */
    PageResult<ShopInvoiceListVo> getPage(PageParam pageParam, ShopInvoiceSearchParam searchParam);

    /**
     * 发票详情
     * @param id 主键ID
     * @return ShopInvoiceInfoVo
     */
    ShopInvoiceInfoVo getInfo(Integer id);

    /**
     * 开票
     * @param id
     * @param shopInvoiceParam
     */
    void invoicing(Integer id, ShopInvoiceParam shopInvoiceParam);

}
