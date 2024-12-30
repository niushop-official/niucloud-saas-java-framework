package com.niu.shop.service.api.goods.vo;

import cn.hutool.core.date.DateUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsBrowseListVo implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private BigDecimal price;

    private BigDecimal marketPrice;

    private Object memberPrice;

    private String memberDiscount;

    public String getGoodsCoverThumbMid() {
        return SpringContext.bean(ICoreUploadService.class).thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
    }

    public String getBrowseTime() {
        return DateUtil.formatDate(new Date(this.browseTime * 1000));
    }
}
