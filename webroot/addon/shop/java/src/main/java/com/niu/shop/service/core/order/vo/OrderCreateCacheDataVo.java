package com.niu.shop.service.core.order.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class OrderCreateCacheDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderKey = "";

    private Integer orderId;

    private Integer siteId;

    private Integer memberId;

    private String orderFrom;

    private List<Integer> cartIds;

    private JSONObject discount;

    private JSONObject extendData;

    private Basic basic = new Basic();

    private Buyer buyer;

    private Map<Integer, Sku> goodsData;

    private Delivery delivery = new Delivery();

    private List<String> error = new LinkedList<>();

    private String memberRemark;

    private String OrderType;

    private Object invoice;

    @Data
    public static class Basic {
        private String body = "";

        private BigDecimal deliveryMoney = new BigDecimal(0);

        private BigDecimal discountMoney = new BigDecimal(0);

        private BigDecimal goodsMoney = new BigDecimal(0);

        private BigDecimal orderMoney = new BigDecimal(0);

        private Integer totalNum = 0;

        private List<String> hasGoodsTypes = new LinkedList<>();
    }

    @Data
    public static class Buyer {
        private String nickname;

        private Integer point;

        private MemberLevelInfoVo memberLevel;
    }

    @Data
    public static class Sku {
        private Integer skuId; // 商品sku_id
        private Integer num;
        private Integer siteId; // 站点id
        private String skuName; // 商品sku名称
        private String skuImage; // sku主图
        private String skuNo; // 商品sku编码
        private Integer goodsId; // 商品id
        private String skuSpecFormat; // sku规格格式
        private BigDecimal price; // sku单价
        private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
        private BigDecimal originalPrice;
        private Integer stock; // 商品sku库存
        private BigDecimal weight; // 重量（单位kg）
        private BigDecimal volume; // 体积（单位立方米）
        private BigDecimal memberPrice;
        private BigDecimal discountMoney = new BigDecimal(0);
        private BigDecimal goodsMoney;
        private Goods goods;
        private Integer notSupportDelivery = 0;
        private BigDecimal deliveryMoney = new BigDecimal(0);
        private Integer isFreeShipping = 0;
    }

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
        private String attrFormat; // 商品参数内容，json格式
        private Integer isDiscount; // 是否参与限时折扣
        private String memberDiscount; // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsCoverThumbSmall() {
            return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        }

        public String getGoodsCoverThumbMid() {
            return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
        }

        public String getGoodsCoverThumbBig() {
            return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());
        }
    }

    @Data
    public static class Delivery {
        private List<DeliveryConfigVo> deliveryTypeList = new LinkedList();

        private String deliveryType = "";

        private ShopStore takeStore;

        private Object takeAddress;
    }
}
