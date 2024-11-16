package com.niu.shop.service.core.marketing;


import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;

import java.util.List;

/**
 * 营销活动接口
 */
public interface ICoreActiveService {

    /**
     * 添加营销活动
     * @param shopActive
     * @param shopActiveGoodsList
     */
    Integer add(ShopActive shopActive, List<ShopActiveGoods> shopActiveGoodsList);

    /**
     * 编辑营销活动
     * @param shopActive
     * @param shopActiveGoodsList
     */
    void edit(ShopActive shopActive, List<ShopActiveGoods> shopActiveGoodsList);

    /**
     * 删除营销活动
     * @param activeId
     * @param siteId
     * @param isForce
     */
    void del(Integer activeId, Integer siteId, Integer isForce);

    /**
     * 活动关闭
     * @param activeId
     * @param siteId
     */
    void close(Integer activeId, Integer siteId);

    /**
     * 活动开启
     * @param activeId
     */
    void start(Integer activeId);

    /**
     * 活动关闭
     * @param activeId
     */
    void end(Integer activeId);


}
