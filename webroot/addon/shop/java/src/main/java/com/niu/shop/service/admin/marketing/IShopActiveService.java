package com.niu.shop.service.admin.marketing;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.service.admin.marketing.param.ShopActiveParam;
import com.niu.shop.service.admin.marketing.param.ShopDiscountSearchParam;
import com.niu.shop.service.admin.marketing.vo.ShopDiscountInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopDiscountListVo;

/**
 * 店铺营销活动（整体活动）服务接口
 */
public interface IShopActiveService extends IService<ShopActive> {


    /**
     * 店铺营销活动（整体活动）列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    PageResult<ShopDiscountListVo> list(PageParam pageParam, ShopDiscountSearchParam searchParam);

    /**
     * 店铺营销活动（整体活动）详情
     *
     * @param id 主键ID
     * @return ShopActiveInfoVo
     */
    ShopDiscountInfoVo info(Integer id);

    /**
     * 店铺营销活动（整体活动）添加
     *
     * @param addParam 添加参数
     */
    void add(ShopActiveParam addParam);

    /**
     * 店铺营销活动（整体活动）编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopActiveParam editParam);

    /**
     * 店铺营销活动（整体活动）删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

}
