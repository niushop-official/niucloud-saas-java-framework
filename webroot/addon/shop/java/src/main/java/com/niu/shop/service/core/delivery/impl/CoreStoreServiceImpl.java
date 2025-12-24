package com.niu.shop.service.core.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.service.admin.delivery.param.TradeTimeItemParam;
import com.niu.shop.service.core.delivery.ICoreStoreService;
import com.niu.shop.service.core.delivery.param.LatlngParam;
import com.niu.shop.service.core.delivery.vo.StoreListVo;
import com.niu.shop.service.core.delivery.vo.TimeListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        List<ShopStore> list = shopStoreMapper.selectList(new QueryWrapper<ShopStore>()
                .eq("site_id", siteId));
        List<StoreListVo> storeList = CollectUtils.convert(list, StoreListVo.class);
        if (ObjectUtil.isNotEmpty(latlng.getLat()) && latlng.getLat().compareTo(BigDecimal.ZERO) == 1 && ObjectUtil.isNotEmpty(latlng.getLng()) && latlng.getLng().compareTo(BigDecimal.ZERO) == 1) {
            for (StoreListVo item : storeList) {
                item.setDistance(calculateDistance(
                        latlng.getLat().doubleValue(),
                        latlng.getLng().doubleValue(),
                        Double.valueOf(item.getLatitude()),
                        Double.valueOf(item.getLongitude())
                ).intValue());
            }
            storeList.sort(Comparator.comparing(StoreListVo::getDistance));
        }
        for(int i=0; i<storeList.size(); i++){
            if(storeList.get(i).getStoreId() == list.get(i).getStoreId()){
                storeList.get(i).setTimeWeek(JSONUtil.parseArray(list.get(i).getTimeWeek()).toList(String.class));
                storeList.get(i).setTradeTimeJson(JSONUtil.parseArray(list.get(i).getTradeTimeJson()).toList(TradeTimeItemParam.class));

                // 组装store_time_list数据
                // 获取当天开始时间（00:00:00）的时间戳（毫秒）
                long dayStartTime = LocalDate.now()
                        .atStartOfDay()
                        .toInstant(ZoneOffset.ofTotalSeconds(0)) // 使用UTC时区
                        .toEpochMilli();
                // 如果需要与PHP一样的秒级时间戳，可以除以1000
                long dayStartTimeInSeconds = dayStartTime / 1000;
                List<TimeListVo> storeTimeList = formatTime(storeList.get(i).getTimeWeek(),storeList.get(i).getTradeTimeJson(),dayStartTimeInSeconds,storeList.get(i).getTimeInterval());
                storeList.get(i).setStoreTimeList(storeTimeList);
            }
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
    public Double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
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

    private List<TimeListVo> formatTime(List<String> weekList, List<TradeTimeItemParam> hourArr,
                                                 long startTime, int interval) {
        if (ObjectUtil.isNull(weekList) || weekList.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用TreeMap自动按时间戳排序
        Map<Long, TimeListVo> resultMap = new TreeMap<>();
        // 获取当前星期几（0-6，对应周日到周六，与PHP的date('w')一致）
        int dayWeek = LocalDate.now().getDayOfWeek().getValue() % 7;

        for (String week : weekList) {
            // 获取星期名称
            Integer weekInt = Integer.parseInt(week);
            String weekName = DeliveryDict.getWeekList(weekInt).getName();
            String dayStr;
            long time;

            if (dayWeek == weekInt) {
                // 今天
                dayStr = "今天";
                time = startTime;
            } else if (dayWeek < weekInt) {
                // 本周内的未来几天
                int day = weekInt - dayWeek;
                time = startTime + (long) day * 86400; // 加相应天数的秒数（1天=86400秒）

                if (day == 1) {
                    dayStr = "明天";
                } else {
                    // 格式化日期为yyyy-MM-dd
                    dayStr = formatTimestampToDate(time);
                }
            } else {
                // 下一周的对应日期
                int day = dayWeek - weekInt;
                time = startTime - (long) day * 86400 + 7 * 86400; // 减天数差再加一周
                dayStr = formatTimestampToDate(time);
            }

            // 构建当前时间项
            TimeListVo timeItem = new TimeListVo();
            timeItem.setName(dayStr + "(" + weekName + ")");
            timeItem.setTimeList(new ArrayList<>());

            resultMap.put(time, timeItem);
        }

        // 返回排序后的值列表
        return new ArrayList<>(resultMap.values());
    }

    /**
     * 将时间戳（秒）格式化为yyyy-MM-dd字符串
     * @param timestamp 秒级时间戳
     * @return 格式化后的日期字符串
     */
    private String formatTimestampToDate(long timestamp) {
        return LocalDate.ofEpochDay(timestamp / 86400)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 生成时间列表（与PHP原方法中的getTimeArr对应）
     * 此处为示例实现，具体逻辑需根据实际业务补充
     */
    private List<String> getTimeArr(List<TradeTimeItemParam> hourArr, long time, int interval) {
        // 实际业务逻辑实现
        List<String> timeList = new ArrayList<>();
        // 示例：根据小时数组和时间戳生成具体时间
        for (TradeTimeItemParam hour : hourArr) {
            timeList.add(formatTimestampToDate(time) + " " + hour + ":00");
        }
        return timeList;
    }

    // 假设的星期数据字典类，与PHP的DeliveryDict对应
    public static class DeliveryDict {
        // 模拟获取星期名称的方法
        public static WeekInfo getWeekList(int week) {
            String[] weekNames = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            return new WeekInfo(weekNames[week]);
        }

        public static class WeekInfo {
            private String name;
            public WeekInfo(String name) {
                this.name = name;
            }
            public String getName() {
                return name;
            }
        }
    }

}
