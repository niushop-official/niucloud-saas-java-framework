package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品规格项/值实体
 */
@Data
public class ShopGoodsSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="spec_id", type= IdType.AUTO)
    private Integer specId; // 规格id
    private Integer siteId; // 站点id
    private Integer goodsId; // 关联商品id
    private String specName; // 规格项名称
    private String specValues; // 规格值名称，多个逗号隔开

}