package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单退款日志实体
 */
@Data
public class ShopOrderRefundLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // id
    private String orderRefundNo; // 退款编号
    private String mainType; // 
    private Integer mainId; // 操作人id
    private Integer status; // 退款状态
    private String type; // 
    private String content; // 日志内容
    private Long createTime; // 创建时间

}