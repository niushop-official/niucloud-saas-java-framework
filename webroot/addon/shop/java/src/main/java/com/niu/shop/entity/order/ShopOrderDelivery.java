package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单发货实体
 */
@Data
public class ShopOrderDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // id
    private Integer siteId; // 站点id
    private Integer orderId; // 
    private String name; // 包裹名称
    private String deliveryType; // 配送方式
    private String subDeliveryType; // 详细配送方式
    private Integer expressCompanyId; // 快递公司id
    private String expressNumber; // 配送单号
    private Integer localDeliverId; // 同城配送员
    private Integer status; // 配送状态
    private Long createTime; // 创建时间
    private String remark; // 

}