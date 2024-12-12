package com.niu.shop.service.core.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.service.core.delivery.ICoreStoreService;
import com.niu.shop.service.core.delivery.param.LatlngParam;
import com.niu.shop.service.core.delivery.vo.StoreListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class CoreStoreServiceImpl implements ICoreStoreService {

    @Resource
    ShopStoreMapper shopStoreMapper;

    @Override
    public void calculate(OrderCreateDataVo vo) {
        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeStore())) {
            vo.getError().add("请选择要自提的门店");
        }

        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
            OrderCreateDataVo.Goods goods = item.getGoods();
            if (goods.getGoodsType().equals("real")) {
                JSONArray deliveryType = JSONUtil.parseArray(goods.getDeliveryType());
                if (!deliveryType.contains(OrderDeliveryTypeEnum.STORE.getType())) {
                    item.setNotSupportDelivery(1);
                    vo.getError().add("不支持选择的配送方式");
                }
            }
        }
    }

    /**
     * 获取自提点列表
     *
     * @param siteId
     * @param latlng
     * @return
     */
    @Override
    public List<StoreListVo> getStoreList(Integer siteId, LatlngParam latlng) {
        List<StoreListVo> storeList = CollectUtils.convert(shopStoreMapper.selectList(new QueryWrapper<ShopStore>()
                .eq("site_id", siteId)), StoreListVo.class);
        if (ObjectUtil.isNotEmpty(latlng.getLat()) && latlng.getLat().compareTo(BigDecimal.ZERO) == 1 && ObjectUtil.isNotEmpty(latlng.getLng()) && latlng.getLng().compareTo(BigDecimal.ZERO) == 1) {
            for (StoreListVo item : storeList) {
                item.setDistance(calculateDistance(
                        latlng.getLat().doubleValue(),
                        latlng.getLng().doubleValue(),
                        Double.valueOf(item.getLatitude()),
                        Double.valueOf(item.getLongitude())
                ));
            }
            storeList.sort(Comparator.comparing(StoreListVo::getDistance));
        }
        return storeList;
    }

    /**
     * 计算两个经纬度间的距离
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    private static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double AVG_EARTH_RADIUS_KM = 6371.0;

        // 将角度转换为弧度
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);

        // Haversine formula
        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(deltaLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 返回距离，单位为米
        return AVG_EARTH_RADIUS_KM * c * 1000;
    }

}
