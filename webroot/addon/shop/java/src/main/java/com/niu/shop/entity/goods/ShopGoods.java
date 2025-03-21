package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品实体
 */
@Data
public class ShopGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="goods_id", type= IdType.AUTO)
    private Integer goodsId; // 商品id

    /** 站点id */
    private Integer siteId;

    /** 商品名称 */
    private String goodsName;

    /** 商品类型 */
    private String goodsType;

    /** 副标题 */
    private String subTitle;

    /** 商品封面 */
    private String goodsCover;

    /** 商品图片 */
    private String goodsImage;

    /** 商品视频 */
    private String goodsVideo;

    /** 商品分类 */
    private String goodsCategory;

    /** 商品介绍 */
    private String goodsDesc;

    /** 商品品牌id */
    private Integer brandId;

    /** 标签组 */
    private String labelIds;

    /** 商品服务 */
    private String serviceIds;

    /** 单位 */
    private String unit;

    /** 商品库存（总和） */
    private Integer stock;

    /** 销量 */
    private Integer saleNum;

    /** 虚拟销量 */
    private Integer virtualSaleNum;

    /** 商品状态（1.正常0下架） */
    private Integer status;

    /** 排序 */
    private Integer sort;

    /** 支持的配送方式 */
    private String deliveryType;

    /** 是否免邮 */
    private Integer isFreeShipping;

    /** 运费设置，选择模板：template，固定运费：fixed */
    private String feeType;

    /** 固定运费 */
    private BigDecimal deliveryMoney;

    /** 运费模板 */
    private Integer deliveryTemplateId;

    /** 虚拟商品是否自动发货 */
    private Integer virtualAutoDelivery;

    /** 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销 */
    private String virtualReceiveType;

    /** 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期 */
    private Integer virtualVerifyType;

    /** 虚拟到期时间 */
    private Integer virtualIndate;

    /** 供应商id */
    private Integer supplierId;

    /** 商品参数id */
    private Integer attrId;

    /** 商品参数内容，json格式 */
    private String attrFormat;

    /** 是否参与限时折扣 */
    private Integer isDiscount;

    /** 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price */
    private String memberDiscount;

    /** 海报id */
    private Integer posterId;

    private Integer formId;

    /** 商品是否限购(0:否 1:是) */
    private Integer isLimit;

    /** 限购类型，1：单次限购，2：单人限购 */
    private Integer limitType;

    /** 限购数 */
    private Integer maxBuy;

    /** 起购数 */
    private Integer minBuy;

    /** 商品是否赠品(0:否 1:是) */
    private Integer isGift;

    /** 访问次数（浏览量） */
    private Integer accessNum;

    /** 加入购物车数量 */
    private Integer cartNum;

    /** 支付件数 */
    private Integer payNum;

    /** 支付总金额 */
    private BigDecimal payMoney;

    /** 收藏数量 */
    private Integer collectNum;

    /** 评论数量 */
    private Integer evaluateNum;

    /** 退款件数 */
    private Integer refundNum;

    /** 退款总额 */
    private BigDecimal refundMoney;

    /** 创建时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

    /** 删除时间 */
    private Long deleteTime;
}