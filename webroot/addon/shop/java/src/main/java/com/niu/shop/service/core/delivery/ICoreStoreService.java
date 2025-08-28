package com.niu.shop.service.core.delivery;

import com.niu.shop.service.core.delivery.param.LatlngParam;
import com.niu.shop.service.core.delivery.vo.StoreListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;

import java.util.List;

/**
 * 配送服务接口
 */
public interface ICoreStoreService {

    /**
     * 配送费用计算
     *
     * @return GoodsCategoryConfigVo
     */
    void calculate(OrderCreateDataVo vo);

    /**
     * 获取自提点列表
     *
     */
    List<StoreListVo> getStoreList(Integer siteId, LatlngParam latlng);

    Double calculateDistance(double lat1, double lng1, double lat2, double lng2);
}
