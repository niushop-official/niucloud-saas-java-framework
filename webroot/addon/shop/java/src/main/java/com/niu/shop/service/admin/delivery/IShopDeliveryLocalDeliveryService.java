package com.niu.shop.service.admin.delivery;

import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliveryParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliverySearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryListVo;
import com.niu.shop.service.admin.delivery.vo.ThirdTypeVo;

import java.util.List;
import java.util.Map;

/**
 * 同城配送服务接口
 */
public interface IShopDeliveryLocalDeliveryService {

    /**
     * 同城配送
     * @return ShopDeliveryLocalDeliveryInfoVo
     */
    ShopDeliveryLocalDeliveryInfoVo getLocal();

    /**
     * 同城配送设置
     * @param param
     */
    void setLocal(ShopDeliveryLocalDeliveryParam param);

    /**
     * 查看三方配送初始化信息
     * @return
     */
    List<ThirdTypeVo> getThirdTypeConfig();
}
