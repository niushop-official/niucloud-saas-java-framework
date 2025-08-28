package com.niu.shop.service.admin.marketing.vo;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopActive列表视图
 */
@Data
public class ShopDiscountGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 折扣商品ID
     */
    private Integer discountGoodsId;

    /**
     * 折扣活动ID
     */
    private Integer discountId;

    /**
     * 商品封面图
     */
    private String goodsCover;

    /**
     * 商品小图
     */
    private String goodsCoverThumbSmall;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 参与会员数
     */
    private Integer memberNum;

    /**
     * 订单总金额
     */
    private BigDecimal orderMoney;

    /**
     * 订单数量
     */
    private Integer orderNum;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * SKU ID
     */
    private Integer skuId;

    /**
     * 成交订单数
     */
    private Integer successNum;

    private ICoreUploadService coreUploadService() {
        return SpringContext.bean(ICoreUploadService.class);
    }

    public String getGoodsCoverThumbSmall() {
        return CommonUtils.thumbImageSmall(this.siteId, this.getGoodsCover());
    }
}
