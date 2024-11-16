package com.niu.shop.service.core.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.member.MemberAddress;
import com.niu.shop.entity.delivery.ShopDeliveryLocalDelivery;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryLocalDeliveryMapper;
import com.niu.shop.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.shop.service.core.delivery.vo.LocalInfoVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Service
public class CoreLocalDeliveryServiceImpl implements ICoreLocalDeliveryService {

    @Resource
    ShopDeliveryLocalDeliveryMapper shopDeliveryLocalDeliveryMapper;

    @Override
    public void calculate(OrderCreateDataVo vo) {
        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeAddress())) {
            vo.getError().add("需要先选择收货地址");
            return;
        }

        BigDecimal goodsMoney = new BigDecimal(0);
        BigDecimal goodsWeight = new BigDecimal(0);

        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
            OrderCreateDataVo.Goods goods = item.getGoods();
            if (goods.getGoodsType().equals("real")) {
                JSONArray deliveryType = JSONUtil.parseArray(goods.getDeliveryType());
                if (deliveryType.contains(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                    goodsMoney = goodsMoney.add(item.getGoodsMoney());
                    goodsWeight = goodsWeight.add(item.getWeight().multiply(new BigDecimal(item.getNum())));
                } else {
                    item.setNotSupportDelivery(1);
                    vo.getError().add("不支持选择的配送方式");
                }
            }
        }

        feeCalculate(vo, goodsMoney, goodsWeight);
    }

    private void feeCalculate(OrderCreateDataVo vo, BigDecimal goodsMoney, BigDecimal goodsWeight) {
        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();

        ShopDeliveryLocalDelivery model = shopDeliveryLocalDeliveryMapper.selectOne(new QueryWrapper<ShopDeliveryLocalDelivery>().eq("site_id", vo.getSiteId()));
        if (model == null) {
            vo.getError().add("商家未配置同城配送");
            return;
        }
        LocalInfoVo local = new LocalInfoVo();
        BeanUtil.copyProperties(model, local);
        local.setAreas(JSONUtil.toList(JSONUtil.parseArray(model.getArea()), LocalInfoVo.Area.class));
        local.setCenters(JSONUtil.toBean(JSONUtil.parseObj(model.getCenter()), LocalInfoVo.LatLnt.class));

        LocalInfoVo.Area inArea = null;

        GeometryFactory geometryFactory = new GeometryFactory();
        // 创建点对象
        Point point = geometryFactory.createPoint(new Coordinate(Double.valueOf(address.getLat()), Double.valueOf(address.getLng())));

        for (LocalInfoVo.Area item : local.getAreas()) {
            if (item.getAreaType().equals("radius")) {
                double distance = calculateDistance(
                        Double.valueOf(address.getLat()),
                        Double.valueOf(address.getLng()),
                        Double.valueOf(item.getAreaJson().getCenter().getLat().doubleValue()),
                        Double.valueOf(item.getAreaJson().getCenter().getLng().doubleValue())
                );
                if (new BigDecimal(item.getAreaJson().getRadius()).compareTo(new BigDecimal(distance * 1000)) != -1) {
                    inArea = item;
                    break;
                }
            } else {
                // 创建电子围栏
                List<Coordinate> polygonCoordinates = new LinkedList<>();
                for (LocalInfoVo.LatLnt latLng: item.getAreaJson().getPaths()) {
                    polygonCoordinates.add(new Coordinate(latLng.getLat().doubleValue(), latLng.getLng().doubleValue()));
                }
                Polygon polygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]));
                // 判断收货地址是否在范围内
                if (polygon.contains(point)) {
                    inArea = item;
                    break;
                }
            }
        }

        if (inArea == null) {
            vo.getError().add("所选的收货地址不支持配送");
            return;
        }

        // 判断是否满足起送价
        if (goodsMoney.compareTo(inArea.getStartPrice()) == -1) {
            vo.getError().add("未达到起送费用");
            return;
        }

        BigDecimal deliveryMoney = new BigDecimal(0);
        // 按距离收费
        // 计算收货地址与取货地址的距离
        if (local.getFeeType().equals("distance")) {
            deliveryMoney = deliveryMoney.add(local.getBasePrice());
            BigDecimal distance = new BigDecimal(calculateDistance(
                    Double.valueOf(address.getLat()),
                    Double.valueOf(address.getLng()),
                    Double.valueOf(local.getCenters().getLat().doubleValue()),
                    Double.valueOf(local.getCenters().getLng().doubleValue())
            ));
            if (distance.compareTo(local.getBaseDist()) == 1) {
                BigDecimal gradPrice = new BigDecimal(Math.ceil(distance.subtract(local.getBaseDist()).divide(local.getGradDist(), 2, BigDecimal.ROUND_HALF_UP).doubleValue())).multiply(local.getGradPrice());
                deliveryMoney = deliveryMoney.add(gradPrice);
            }
        } else {
            deliveryMoney = deliveryMoney.add(inArea.getDeliveryPrice());
        }

        // 计算续重费用
        BigDecimal zero = new BigDecimal(0);
        if (local.getWeightStart().compareTo(zero) == 1
                && local.getWeightUnit().compareTo(zero) == 1
                && local.getWeightPrice().compareTo(zero) == 1
                && local.getWeightStart().compareTo(goodsWeight) == -1
        ) {
            BigDecimal weightPrice = new BigDecimal(Math.ceil(goodsWeight.subtract(local.getWeightStart()).divide(local.getWeightUnit(), 2, BigDecimal.ROUND_HALF_UP).doubleValue())).multiply(local.getWeightPrice());
            deliveryMoney = deliveryMoney.add(weightPrice);
        }

        vo.getBasic().setDeliveryMoney(deliveryMoney);
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
