package com.niu.shop.entity.shop_address;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商家地址库实体
 */
@Data
public class ShopAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 
    private Integer siteId; // 站点id
    private String contactName; // 联系人
    private String mobile; // 手机号
    private Integer provinceId; // 省
    private Integer cityId; // 市
    private Integer districtId; // 区
    private String address; // 详细地址
    private String fullAddress; // 地址
    private String lat; // 纬度
    private String lng; // 经度
    private Integer isDeliveryAddress; // 是否是发货地址
    private Integer isRefundAddress; // 是否是退货地址
    private Integer isDefaultDelivery; // 默认发货地址
    private Integer isDefaultRefund; // 默认收货地址

}