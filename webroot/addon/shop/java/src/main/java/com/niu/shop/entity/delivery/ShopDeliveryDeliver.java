package com.niu.shop.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 配送员实体
 */
@Data
public class ShopDeliveryDeliver implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="deliver_id", type= IdType.AUTO)
    private Integer deliverId; // 配送员id
    private Integer siteId; // 站点id
    private String deliverName; // 配送员名称
    private String deliverMobile; // 配送员手机号
    private Long createTime; // 创建时间
    private Long modifyTime; // 修改时间
    private Integer storeId; // 门店id

}