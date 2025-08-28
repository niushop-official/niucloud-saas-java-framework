package com.niu.shop.service.api.goods.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.CommonUtils;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@Data
public class ShopGoodsSkuPaihangVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer skuId; // 商品sku_id
    private Integer siteId; // 站点id
    private String skuName; // 商品sku名称
    private String skuImage; // sku主图
    private String skuNo; // 商品sku编码
    private Integer goodsId; // 商品id
    private String skuSpecFormat; // sku规格格式
    private BigDecimal price; // sku单价
    private BigDecimal showPrice; // 显示价格（有活动显示活动价，默认原价）
    private String showType;
    private BigDecimal marketPrice; // 划线价
    private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
    private BigDecimal costPrice; // sku成本价
    private Integer stock; // 商品sku库存
    private BigDecimal weight; // 重量（单位kg）
    private BigDecimal volume; // 体积（单位立方米）
    private Integer saleNum; // 销量
    private Integer isDefault; // 是否默认
    private String memberPrice; // 会员价，json格式，指定会员价，数据结构为：{"level_1":"10.00","level_2":"10.00"}

}
