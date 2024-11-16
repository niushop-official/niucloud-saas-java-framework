package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单日志实体
 */
@Data
public class ShopOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // id
    private Integer orderId; // 订单id
    private String mainType; // 
    private Integer mainId; // 操作人id
    private Integer status; // 订单状态
    private String type; // 
    private String content; // 日志内容
    private Long createTime; // 创建时间

}