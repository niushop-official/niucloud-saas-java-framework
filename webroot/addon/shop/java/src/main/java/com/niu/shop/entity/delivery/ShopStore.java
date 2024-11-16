package com.niu.shop.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 自提门店实体
 */
@Data
public class ShopStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="store_id", type= IdType.AUTO)
    private Integer storeId; // 
    private Integer siteId; // 站点id
    private String storeName; // 门店名称
    private String storeDesc; // 简介
    private String storeLogo; // 门店logo
    private String storeMobile; // 手机号
    private Integer provinceId; // 省id
    private Integer cityId; // 市
    private Integer districtId; // 县（区）
    private String address; // 详细地址
    private String fullAddress; // 完整地址
    private String longitude; // 经度
    private String latitude; // 纬度
    private String tradeTime; // 营业时间
    private Long createTime; // 添加时间
    private Long updateTime; // 更新时间

}