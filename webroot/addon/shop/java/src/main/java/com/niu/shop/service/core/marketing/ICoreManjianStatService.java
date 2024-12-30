package com.niu.shop.service.core.marketing;

import java.util.Map;

/**
 * core满减统计服务接口
 */
public interface ICoreManjianStatService {

        /**
         * 统计方法，调用set方法来进行实际的数据更新操作
         *
         * @param siteId    站点ID
         * @param manjianId 满减活动ID
         * @param data      包含统计数据的Map
         * @return true表示操作成功
         */
        boolean stat(Integer siteId, Integer manjianId, Map<String, String> data);

        /**
         * 实际进行数据更新设置的方法
         *
         * @param siteId    站点ID
         * @param manjianId 满减活动ID
         * @param data      包含要更新数据的Map
         * @return true表示操作成功
         */
        boolean set(Integer siteId, Integer manjianId, Map<String, String> data);

}
