package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品收藏记录实体
 */
@Data
public class ShopGoodsCollect implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 
    private Integer siteId; // 站点id
    private Integer memberId; // 会员id
    private Integer goodsId; // 商品id
    private Long createTime; // 收藏时间

}