package com.niu.shop.service.api.goods.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ShopGoodsEvaluate视图
 */
@Data
public class ShopGoodsEvaluateDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer evaluateId;  // 
    private Integer siteId;  // 站点id
    private Integer orderId;  // 订单id
    private Integer orderGoodsId;  // 订单项ID
    private Integer goodsId;  // 商品ID
    private Integer memberId;  // 会员ID
    private String memberHead;  // 会员头像
    private String memberName;  // 会员名称
    private String content;  // 评价内容
    private String[] images;  // 评价图片
    private List<String> imageMid = new ArrayList<>();
    private List<String> imageBig = new ArrayList<>();
    private Integer isAnonymous;  // 1匿名  2不匿名
    private Integer scores;  // 评论分数 1-5
    private Integer isAudit;  // 审核状态 1待审 2通过 3拒绝
    private String explainFirst;  // 解释内容
    private Integer topping;  // 排序 置顶
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 评论时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 修改时间
    private ShopGoodsEvaluateDetailVo.OrderGoods orderGoods; // 订单商品信息


    @Data
    public static class OrderGoods {
        private Integer orderGoodsId; //
        private Integer siteId; // 站点id
        private Integer orderId; // 订单id
        private Integer memberId; // 购买会员id
        private Integer goodsId; // 商品id
        private Integer skuId; // 商品规格id
        private String goodsName; // 商品名称
        private String skuName; // 商品规格名称
        private String goodsImage; // 商品图片
        private String goodsImageThumbMid;
        private String skuImage; // sku规格图片
        private BigDecimal price; // 商品单价
        private Integer num; // 购买数量
        private BigDecimal goodsMoney; // 商品总价
        private Integer isEnableRefund; // 是否允许退款
        private Integer deliveryId;
        private Integer status; // 状态
        private String orderRefundNo; // 退款单号
        private String goodsType; // 商品类型
        private String deliveryStatus; // 配送状态
        private Integer verifyCount; // 已核销次数
        private Integer verifyExpireTime; // 过期时间 0 为永久
        private Integer isVerify; // 是否需要核销

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsImageThumbMid() {
            return coreUploadService().thumb(this.siteId, this.getGoodsImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
        }
    }
}
