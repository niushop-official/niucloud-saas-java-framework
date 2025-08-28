package com.niu.shop.service.api.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.service.core.order.vo.DeliveryTraceVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DeliveryPackageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id; // id
    private Integer siteId; // 站点id
    private Integer orderId; //
    private String name; // 包裹名称
    private String deliveryType; // 配送方式
    private String subDeliveryType; // 详细配送方式
    private Integer expressCompanyId; // 快递公司id
    private String expressNumber; // 配送单号
    private Integer localDeliverId; // 同城配送员
    private Integer status; // 配送状态
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private String remark; //
    private Company company;
    private List<OrderGoods> orderGoods;
    private DeliveryTraceVo traces;

    @Data
    public static class Company {
        private Integer companyId; //
        private String companyName; // 物流公司名称
        private String expressNo; // 站点id
    }

    @Data
    public static class OrderGoods {
        private Integer skuId; // 商品规格id
        private String goodsName; // 商品名称
        private String skuName; // 商品规格名称
        private String goodsImage; // 商品图片
        private String goodsImageThumbSmall;
        private Integer num; // 购买数量
        private Integer deliveryId;
        private BigDecimal price;
        private Integer siteId;


        public String getGoodsImageThumbSmall() {
            return CommonUtils.thumbImageSmall(this.siteId, this.goodsImage);
        }
    }
}
