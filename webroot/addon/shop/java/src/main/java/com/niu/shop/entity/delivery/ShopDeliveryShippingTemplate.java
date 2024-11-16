package com.niu.shop.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 运费模板实体
 */
@Data
public class ShopDeliveryShippingTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="template_id", type= IdType.AUTO)
    private Integer templateId; // 
    private Integer siteId; // 站点id
    private String templateName; // 模板名称
    private String feeType; // 运费计算方式1.重量2体积3按件
    private Long createTime; // 创建时间
    private Long updateTime; // 修改时间
    private Integer isFreeShipping; // 该区域是否包邮
    private Integer noDelivery; // 是否指定该区域不配送

}