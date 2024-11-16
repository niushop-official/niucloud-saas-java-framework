package com.niu.shop.service.admin.marketing;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeEditStatusParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeModifySortParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeListVo;

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

}
