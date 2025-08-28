package com.niu.shop.service.api.goods.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品收藏记录实体
 */
@Data
public class ShopGoodsCollectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 
    private Integer siteId; // 站点id
    private Integer memberId; // 会员id
    private Integer goodsId; // 商品id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 收藏时间
    private String goodsName;
    private String goodsCover;
    private String skuName;
    private BigDecimal price;
    private BigDecimal showPrice;
    private String showType;
    private Integer status;
    private String memberPrice;
    private String memberDiscount;
    private Integer skuId;

    public String getGoodsCoverThumbMid() {
        return CommonUtils.thumbImageMiddle(this.siteId, this.getGoodsCover());
    }

}