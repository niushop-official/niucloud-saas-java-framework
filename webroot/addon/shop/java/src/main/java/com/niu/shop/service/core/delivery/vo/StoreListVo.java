package com.niu.shop.service.core.delivery.vo;

import com.niu.shop.service.admin.delivery.param.TradeTimeItemParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class StoreListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer storeId; //
    private String storeName; // 门店名称
    private String storeDesc; // 简介
    private String storeLogo; // 门店logo
    private String storeMobile; // 手机号
    private String fullAddress; // 完整地址
    private String longitude; // 经度
    private String latitude; // 纬度
    private String tradeTime; // 营业时间
    private double distance;
    private List<String> timeWeek; //自定义的营业时间["0","1","2","3","4","5","6"]周日-周六
    private List<TradeTimeItemParam> tradeTimeJson; //营业时间JSON
    private Integer timeInterval; //时段设置（分钟）
    private List<TimeListVo> storeTimeList;
    private Integer provinceId; //省id
    private Integer cityId; //市
    private Integer districtId; //县
    private String address; //详细地址
}
