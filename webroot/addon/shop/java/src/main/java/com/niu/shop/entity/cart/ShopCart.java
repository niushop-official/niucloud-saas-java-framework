package com.niu.shop.entity.cart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 购物车实体
 */
@Data
public class ShopCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Long id; // 购物车表ID
    private Integer siteId; // 站点id
    private Integer memberId; // 会员ID
    private Integer goodsId; // 商品ID
    private Integer skuId; // sku id
    private Integer num; // 商品数量
    private Integer marketType; // 活动类型
    private Integer marketTypeId; // 活动id
    private Long createTime; // 添加时间
    private Integer status; // 购物车商品状态
    private String invalidRemark; // 失效原因

}