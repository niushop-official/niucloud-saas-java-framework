package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

/**
 * 商品浏览历史实体
 */
@Data
public class ShopGoodsBrowse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**  */
    private Integer siteId;

    /** 浏览人 */
    private Integer memberId;

    /** sku_id */
    private Integer skuId;

    /** 商品id */
    private Integer goodsId;

    /** 浏览时间 */
    private Long browseTime;

    /** 商品图片 */
    private String goodsCover;

    /** 商品名称 */
    private String goodsName;

}