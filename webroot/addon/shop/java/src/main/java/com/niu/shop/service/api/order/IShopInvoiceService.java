package com.niu.shop.service.api.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.order.param.ShopInvoiceAddParam;
import com.niu.shop.service.api.order.param.ShopInvoiceEditParam;
import com.niu.shop.service.api.order.param.ShopInvoiceParam;
import com.niu.shop.service.api.order.param.ShopInvoiceSearchParam;
import com.niu.shop.service.api.order.vo.ShopInvoiceInfoVo;
import com.niu.shop.service.api.order.vo.ShopInvoiceListVo;

import java.math.BigDecimal;
import java.util.List;

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
     * @param param
     */
    void apply(ShopInvoiceAddParam param);

    /**
     * 取消开票
     * @param id
     */
    void cancel(Integer id);
    /**
     * 修改发票信息
     * @param param
     */
    void edit(ShopInvoiceEditParam param);

}
