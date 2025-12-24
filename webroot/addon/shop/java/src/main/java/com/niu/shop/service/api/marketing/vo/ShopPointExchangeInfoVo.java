package com.niu.shop.service.api.marketing.vo;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.service.api.goods.vo.GoodsLabelListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class ShopPointExchangeInfoVo {

    private Integer skuId; // 商品sku_id
    private Integer siteId; // 站点id
    private String skuName; // 商品sku名称
    private String skuImage; // sku主图
    private String skuNo; // 商品sku编码
    private Integer goodsId; // 商品id
    private String memberDiscount; // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
    private String skuSpecFormat; // sku规格格式
    private BigDecimal price; // sku单价
    private BigDecimal marketPrice; // 划线价
    private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
    private BigDecimal costPrice; // sku成本价
    private Integer stock; // 商品sku库存
    private Integer point; // 商品sku库存
    private BigDecimal weight; // 重量（单位kg）
    private BigDecimal volume; // 体积（单位立方米）
    private Integer saleNum; // 销量
    private Integer isDefault; // 是否默认
    private BigDecimal memberPrice;
    private Integer evaluateIsShow;
    private Integer exchangeId;
    private ShopDiscountGoodsInfoVo discountInfo;
    private Integer isLimit = 0;
    private Integer limitType = 1;
    private Integer maxBuy = 0;
    private Integer minBuy = 0;
    private BigDecimal showPrice;
    private String showType;


    @JsonProperty("skuList")
    protected List<Sku> skuList;

    private Goods goods;

    @JsonProperty("goodsSpec")
    private List<Spec> goodsSpec;

    private List<Service> service = new LinkedList<>();

    private List<GoodsLabelListVo> labelInfo = new LinkedList<>();

    private Brand brandInfo;
    private int isNewcomer;
    private String type;
    private String typeName;

    private String newcomerPrice;

    private String newcomerDesc;

    private Object sowShowList;

    private Object diyDetailInfo;


    private ImageSize imageSize = new ImageSize();

    @Data
    public static class Goods {
        private Integer goodsId; // 商品id
        private Integer siteId; // 站点id
        private String goodsName; // 商品名称
        private String goodsType; // 商品类型
        private String subTitle; // 副标题
        private String goodsCover; // 商品封面
        private String goodsCoverThumbBig;
        private String goodsCoverThumbSmall;
        private String goodsCoverThumbMid;
        private String goodsImage; // 商品图片
        private List<String> goodsImageThumbBig;
        private List<String> goodsImageThumbSmall;
        private List<String> goodsImageThumbMid;
        private String goodsVideo;
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String goodsCategory; // 商品分类
        private String goodsDesc; // 商品介绍
        private Integer brandId; // 商品品牌id
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String labelIds; // 标签组
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String serviceIds; // 商品服务
        private String unit; // 单位
        private Integer stock; // 商品库存（总和）
        private Integer saleNum; // 销量
        private Integer virtualSaleNum; // 虚拟销量
        private Integer status; // 商品状态（1.正常0下架）
        private Integer sort; // 排序
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
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
        private JSONArray attrFormat; // 商品参数内容，json格式
        private Integer isDiscount; // 是否参与限时折扣
        private String memberDiscount; // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
        private Integer posterId; // 海报id
        private Integer isCollect = 0;
        private Integer hasBuy = 0;
        private Integer isLimit = 0;
        private Integer limitType = 1;
        private Integer maxBuy = 0;
        private Integer minBuy = 0;
        private Integer isGift = 0;
        private Integer formId;
        private Map<String, DeliveryConfigVo> deliveryTypeList;

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsCoverThumbSmall() {
            return CommonUtils.thumbImageSmall(this.siteId, this.getGoodsCover());
        }

        public String getGoodsCoverThumbMid() {
            return CommonUtils.thumbImageMiddle(this.siteId, this.getGoodsCover());
        }

        public String getGoodsCoverThumbBig() {
            return CommonUtils.thumbImageBig(this.siteId, this.getGoodsCover());
        }

        public List<String> getGoodsImageThumbSmall() {
            List<String> list = new LinkedList<>();
            List<String> images = Arrays.asList(this.goodsImage.split(","));
            for (String path : images) {
                list.add(CommonUtils.thumbImageSmall(this.siteId, this.getGoodsCover()));
            }
            return list;
        }

        public List<String> getGoodsImageThumbMid() {
            List<String> list = new LinkedList<>();
            List<String> images = Arrays.asList(this.goodsImage.split(","));
            for (String path : images) {
                list.add(CommonUtils.thumbImageMiddle(this.siteId, this.getGoodsCover()));
            }
            return list;
        }

        public List<String> getGoodsImageThumbBig() {
            List<String> list = new LinkedList<>();
            List<String> images = Arrays.asList(this.goodsImage.split(","));
            for (String path : images) {
                list.add(CommonUtils.thumbImageBig(this.siteId, this.getGoodsCover()));
            }
            return list;
        }

        public Map<String, DeliveryConfigVo> getDeliveryTypeList() {
            ICoreDeliveryConfigService coreDeliveryConfigService = SpringContext.bean(ICoreDeliveryConfigService.class);
            List<DeliveryConfigVo> deliveryList = coreDeliveryConfigService.getDeliveryList(this.siteId);
            if (ObjectUtil.isEmpty(this.deliveryType)) {
                return new HashMap<>();
            }
            JSONArray deliveryType = JSONUtil.parseArray(this.deliveryType);
            return deliveryList.stream().filter(i -> deliveryType.contains(i.getKey()) && i.getStatus().equals(1)).collect(Collectors.toMap(DeliveryConfigVo::getKey, i -> i));
        }
    }

    @Data
    public static class Sku {
        private Integer skuId; // 商品sku_id
        private Integer siteId; // 站点id
        private String skuName; // 商品sku名称
        private String skuImage; // sku主图
        private String skuNo; // 商品sku编码
        private Integer goodsId; // 商品id
        private String skuSpecFormat; // sku规格格式
        private BigDecimal price; // sku单价
        private BigDecimal marketPrice; // 划线价
        private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
        private BigDecimal costPrice; // sku成本价
        private Integer stock; // 商品sku库存
        private Integer limitNum; // 商品sku库存
        private Integer point; // 商品sku库存
        private BigDecimal weight; // 重量（单位kg）
        private BigDecimal volume; // 体积（单位立方米）
        private Integer saleNum; // 销量
        private Integer isDefault; // 是否默认
        private BigDecimal memberPrice;
        private BigDecimal newcomerPrice;
        private int isNewcomer;
        private String type;
        private BigDecimal showPrice;
        private String showType;
    }

    @Data
    public static class Spec extends ShopGoodsSpec {

    }

    @Data
    public static class Service {
        private Integer serviceId; //
        private String serviceName; // 服务名称
        private String image; // 图片
        private String desc; // 描述
    }

    @Data
    public static class Brand {
        private Integer brandId; // 品牌ID
        private String brandName; // 品牌名称
        private String logo; // 品牌logo
        private String desc; // 品牌介绍
    }

    public String getTypeName(){
        return ActiveClassEnum.getNameByType(this.type);
    }

    @Data
    public static class ImageSize {
        private String width = "";
        private String height = "";
    }
}
