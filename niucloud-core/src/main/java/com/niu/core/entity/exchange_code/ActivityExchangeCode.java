package com.niu.core.entity.exchange_code;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动兑换码表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityExchangeCode {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 
     */
    private Integer siteId;
    
    /**
     * 兑换码
     */
    private String code;
    
    /**
     * normal-正常  received-被领取
     */
    private String status;
    
    /**
     * 例seckill-秒杀活动
     */
    private String activityType;
    
    /**
     * 活动ID
     */
    private Integer activityId;
    
    /**
     * 类型    例seckill_goods-秒杀商品
     */
    private String type;
    
    /**
     * 类型对应id  秒杀商品id
     */
    private Integer typeId;
    
    /**
     * 过期时间 0-不过期
     */
    private Long expireTime;
    
    /**
     * 领取会员
     */
    private Integer memberId;
    
    /**
     * 领取时间
     */
    private Long receivedTime;
    
    /**
     * 对应订单id
     */
    private Integer orderId;
    
    /**
     * 操作人名称（添加人）
     */
    private String adminUsername;
    
    /**
     * 操作人id
     */
    private Integer adminId;
    
    /**
     * 创建时间
     */
    private Long createTime;
}