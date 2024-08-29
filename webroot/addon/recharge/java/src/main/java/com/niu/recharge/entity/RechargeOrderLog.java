package com.niu.recharge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    private Integer orderId;

    /** 站点id */
    private Integer siteId;

    /** 操作内容 */
    private String action;

    /** 操作人id */
    private Integer uid;

    /** 操作人名称 */
    private String nickName;

    /** 订单状态 */
    private Integer orderStatus;

    /** 操作类型1买家2卖家 3 系统任务 */
    private Integer actionWay;

    /** 订单状态名称，操作后 */
    private String orderStatusName;

    /** 操作时间 */
    private Long actionTime;

}
