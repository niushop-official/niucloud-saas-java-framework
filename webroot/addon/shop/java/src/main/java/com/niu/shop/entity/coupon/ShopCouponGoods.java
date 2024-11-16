package com.niu.shop.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 优惠券商品或品类关联实体
 */
@Data
public class ShopCouponGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 自增ID
    private Integer siteId; // 站点id
    private Integer couponId; // 优惠券模板id
    private Integer goodsId; // 商品id
    private Integer categoryId; // 分类id

}