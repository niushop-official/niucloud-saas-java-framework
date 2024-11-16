package com.niu.shop.service.admin.delivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetListsParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetPagesParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryElectronicSheetInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryElectronicSheetListVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryElectronicSheetPageVo;
import com.niu.shop.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.shop.service.core.delivery.vo.ElectronicSheetConfigValueVo;
import com.niu.shop.service.core.delivery.vo.ElectronicSheetPrintVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 电子面单服务接口
 */
public interface IShopDeliveryElectronicSheetService {

    List<ShopDeliveryElectronicSheetListVo> lists(PageParam pageParam, ShopDeliveryElectronicSheetListsParam listsParam);

    /**
     * 电子面单列表
     *
     * @param pageParam  分页参数
     * @param pagesParam 搜索参数
     * @return PageResult<ShopDeliveryElectronicSheetListVo>
     */
    PageResult<ShopDeliveryElectronicSheetPageVo> pages(PageParam pageParam, ShopDeliveryElectronicSheetPagesParam pagesParam);

    /**
     * 电子面单详情
     *
     * @param id 主键ID
     * @return ShopDeliveryElectronicSheetInfoVo
     */
    ShopDeliveryElectronicSheetInfoVo info(Integer id);

    /**
     * 电子面单添加
     *
     * @param addParam 添加参数
     */
    void add(ShopDeliveryElectronicSheetParam addParam);

    /**
     * 电子面单编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopDeliveryElectronicSheetParam editParam);

    /**
     * 电子面单删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    Map<Integer, String> getPayType();

    @Transactional
    void setDefault(Integer id);

    ElectronicSheetConfigValueVo getElectronicSheetConfig();

    void setElectronicSheetConfig(Object config);

    List<ElectronicSheetPrintVo> printElectronicSheet(ShopDeliveryPrintElectronicSheetParam param);
}
