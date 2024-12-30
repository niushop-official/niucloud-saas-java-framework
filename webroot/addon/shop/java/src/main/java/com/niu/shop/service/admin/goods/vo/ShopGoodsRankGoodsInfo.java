package com.niu.shop.service.admin.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.shop.entity.goods.ShopGoodsSku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopGoodsRankGoodsInfo {
    private Integer goodsId; // 商品id
    private Integer siteId; // 站点id
    private String goodsName; // 商品名称
    private String goodsType; // 商品类型
    private String subTitle; // 副标题
    private String goodsCover; // 商品封面
    private String goodsImage; // 商品图片
    private String goodsCategory; // 商品分类
    private String goodsDesc; // 商品介绍
    private Integer brandId; // 商品品牌id
    private String labelIds; // 标签组
    private String serviceIds; // 商品服务
    private String unit; // 单位
    private Integer stock; // 商品库存（总和）
    private Integer saleNum; // 销量
    private Integer virtualSaleNum; // 虚拟销量
    private Integer status; // 商品状态（1.正常0下架）
    private Integer sort; // 排序
    private String deliveryType; // 支持的配送方式
    private Integer isFreeShipping; // 是否免邮
    private String feeType; // 运费设置，选择模板：template，固定运费：fixed
    private BigDecimal deliveryMoney; // 固定运费
    private Integer deliveryTemplateId; // 运费模板
    private Integer virtualAutoDelivery; // 虚拟商品是否自动发货
    private String virtualReceiveType; // 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销
    private Integer virtualVerifyType; // 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期
    private Integer virtualIndate; // 虚拟到期时间
    private Integer supplierId; // 供应商id
    private Integer attrId; // 商品参数id
    private String attrFormat; // 商品参数内容，json格式
    private Integer isDiscount; // 是否参与限时折扣
    private String memberDiscount; // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
    private Integer posterId; // 海报id
    private Long createTime; // 创建时间
    private Long updateTime; // 修改时间
    private String goodsTypeName;
    @JsonProperty("goodsSku")
    private ShopGoodsSku goodsSku;
}
