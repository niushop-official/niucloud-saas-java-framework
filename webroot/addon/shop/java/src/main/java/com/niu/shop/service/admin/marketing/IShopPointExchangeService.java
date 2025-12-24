package com.niu.shop.service.admin.marketing;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.exchange.ShopPointExchange;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeListVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeSelectListVo;

import java.util.List;
import java.util.Map;

/**
 * 积分兑换服务接口
 */
public interface IShopPointExchangeService {


    /**
     * 积分兑换列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopPointExchangeListVo>
     */
    PageResult<ShopPointExchangeListVo> list(PageParam pageParam, ShopPointExchangeSearchParam searchParam);

    /**
     * 积分兑换详情
     * @param id 主键ID
     * @return ShopPointExchangeInfoVo
     */
    ShopPointExchangeInfoVo info(Integer id);

    /**
     * 积分兑换添加
     * @param addParam 添加参数
     */
    void add(ShopPointExchangeParam addParam);

    /**
     * 积分兑换编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopPointExchangeParam editParam);

    /**
     * 积分兑换删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 获取积分兑换类型枚举
     */
    Map<String, String> type();

    /**
     * 获取积分兑换状态枚举
     */
    Map<Integer, String> status();

    /**
     * 编辑积分兑换状态
     */
    void editStatus(ShopPointExchangeEditStatusParam editStatusParam);

    /**
     * 修改积分兑换排序
     */
    void modifySort(ShopPointExchangeModifySortParam modifySortParam);

    /**
     * 商品编辑业务
     * @param shopPointExchangeInfo
     * @param status
     * @param skuMap
     */
    void afterGoodsEdit(ShopPointExchange shopPointExchangeInfo, Integer status, Map<Integer, ShopGoodsSku> skuMap);

    /**
     * 批量删除积分兑换
     */
    void batchDelete(PointExchangeBatchParam param);

    /**
     * 批量下架
     * @param param
     */
    void batchDown(PointExchangeBatchParam param);

    /**
     * 批量上架
     * @param param
     */
    void batchUp(PointExchangeBatchParam param);

    /**
     * 积分商城分页列表（用于弹框选择）
     * @param pageParam
     * @param searchParam
     * @return
     */
    ShopPointExchangeSelectListVo getSelectPage(PageParam pageParam, ShopPointExchangeSearchParam searchParam);
}
