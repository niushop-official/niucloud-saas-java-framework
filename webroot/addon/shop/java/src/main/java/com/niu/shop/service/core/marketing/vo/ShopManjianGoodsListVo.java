package com.niu.shop.service.core.marketing.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 满减商品表列表
 */
@Data
public class ShopManjianGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 参与商品 all_goods:全部商品参与  selected_goods:指定商品 selected_goods_not:指定商品不参与
     */
    private String goodsType;

    /**
     * 满减商品活动id
     */
    private Integer manjianGoodsId;

    /**
     * 满减活动id
     */
    private Integer manjianId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 规格id
     */
    private Integer skuId;

    /**
     * 状态（0未开始1进行中2已结束-1已关闭）
     */
    private Integer status;


}
