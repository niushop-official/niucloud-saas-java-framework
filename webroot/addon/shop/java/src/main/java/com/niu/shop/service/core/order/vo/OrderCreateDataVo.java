package com.niu.shop.service.core.order.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.order.param.InvoiceCreateParam;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class OrderCreateDataVo implements Serializable {
    protected static final long serialVersionUID = 1L;

    protected String orderKey = "";

    protected Integer orderId;

    protected Integer siteId;

    protected Integer memberId;

    protected String orderFrom;

    protected List<Integer> cartIds;

    protected Map<String, Discount> discount = new HashMap<>();

    protected JSONObject extendData;

    protected Basic basic = new Basic();

    protected Buyer buyer;

    protected Map<Integer, ? extends Sku> goodsData;

    protected Delivery delivery = new Delivery();

    protected List<String> error = new LinkedList<>();

    protected String memberRemark;

    protected String OrderType;

    protected InvoiceCreateParam invoice;

    @Data
    public static class Basic {
        protected String body = "";

        protected BigDecimal deliveryMoney = new BigDecimal(0);

        protected BigDecimal discountMoney = new BigDecimal(0);

        protected BigDecimal goodsMoney = new BigDecimal(0);

        protected BigDecimal orderMoney = new BigDecimal(0);

        protected Integer totalNum = 0;

        protected List<String> hasGoodsTypes = new LinkedList<>();
    }

    @Data
    public static class Buyer {
        protected String nickname;

        protected Integer point;

        protected MemberLevelInfoVo memberLevel;
    }

    @Data
    public static class Sku {
        protected Integer skuId; // 商品sku_id
        protected Integer num;
        protected Integer siteId; // 站点id
        protected String skuName; // 商品sku名称
        protected String skuImage; // sku主图
        protected String skuNo; // 商品sku编码
        protected Integer goodsId; // 商品id
        protected String skuSpecFormat; // sku规格格式
        protected BigDecimal price; // sku单价
        protected BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
        protected BigDecimal originalPrice;
        protected Integer stock; // 商品sku库存
        protected BigDecimal weight; // 重量（单位kg）
        protected BigDecimal volume; // 体积（单位立方米）
        protected BigDecimal memberPrice;
        protected BigDecimal discountMoney = new BigDecimal(0);
        protected BigDecimal goodsMoney;
        protected Goods goods;
        protected Integer notSupportDelivery = 0;
        protected BigDecimal deliveryMoney = new BigDecimal(0);
        protected Integer isFreeShipping = 0;
    }

    @Data
    public static class Goods {
        protected Integer goodsId; // 商品id
        protected Integer siteId; // 站点id
        protected String goodsName; // 商品名称
        protected String goodsType; // 商品类型
        protected String subTitle; // 副标题
        protected String goodsCover; // 商品封面
        protected String goodsCoverThumbBig;
        protected String goodsCoverThumbSmall;
        protected String goodsCoverThumbMid;
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String goodsCategory; // 商品分类
        protected String goodsDesc; // 商品介绍
        protected Integer brandId; // 商品品牌id
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String labelIds; // 标签组
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String serviceIds; // 商品服务
        protected String unit; // 单位
        protected Integer stock; // 商品库存（总和）
        protected Integer saleNum; // 销量
        protected Integer virtualSaleNum; // 虚拟销量
        protected Integer status; // 商品状态（1.正常0下架）
        protected Integer sort; // 排序
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        protected String deliveryType; // 支持的配送方式
        protected Integer isFreeShipping; // 是否免邮
        protected String feeType; // 运费设置，选择模板：template，固定运费：fixed
        protected BigDecimal deliveryMoney; // 固定运费
        protected Integer deliveryTemplateId; // 运费模板
        protected Integer virtualAutoDelivery; // 虚拟商品是否自动发货
        protected String virtualReceiveType; // 虚拟商品收货方式，auto：自动收货，artificial：买家确认收货，verify：到店核销
        protected Integer virtualVerifyType; // 虚拟商品核销有效期类型，0：不限，1：购买后几日有效，2：指定过期日期
        protected Integer virtualIndate; // 虚拟到期时间
        protected Integer supplierId; // 供应商id
        protected Integer attrId; // 商品参数id
        protected String attrFormat; // 商品参数内容，json格式
        protected Integer isDiscount; // 是否参与限时折扣
        protected String memberDiscount; // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price

        protected ICoreUploadService coreUploadService() {
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
        protected List<DeliveryConfigVo> deliveryTypeList = new LinkedList();

        protected String deliveryType = "";

        protected ShopStore takeStore;

        protected Object takeAddress;
    }

    @Data
    public static class Discount {
        protected Integer[] matchGoodsIds;
        protected String type;
        protected Integer num;
        protected BigDecimal money;
        protected String discountType;
        protected Integer discountTypeId;
        protected String content;
        protected String title;
    }
}
