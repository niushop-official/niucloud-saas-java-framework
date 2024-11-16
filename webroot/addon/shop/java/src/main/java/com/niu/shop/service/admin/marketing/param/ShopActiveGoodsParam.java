package com.niu.shop.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 店铺营销活动参数
*/
@Data
public class ShopActiveGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "activeGoodsId参数缺失")
    private Integer activeGoodsId;

    @NotNull(message = "activeId参数缺失")
    private Integer activeId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "goodsId参数缺失")
    private Integer goodsId;

    @NotNull(message = "activeGoodsType参数缺失")
    private String activeGoodsType;

    @NotNull(message = "activeClass参数缺失")
    private String activeClass;

    @NotNull(message = "activeGoodsLabel参数缺失")
    private String activeGoodsLabel;

    @NotNull(message = "activeGoodsCategory参数缺失")
    private String activeGoodsCategory;

    @NotNull(message = "activeGoodsValue参数缺失")
    private String activeGoodsValue;

    @NotNull(message = "activeGoodsStatus参数缺失")
    private String activeGoodsStatus;

    @NotNull(message = "activeGoodsPoint参数缺失")
    private BigDecimal activeGoodsPoint;

    @NotNull(message = "activeGoodsPrice参数缺失")
    private BigDecimal activeGoodsPrice;

    @NotNull(message = "activeGoodsStock参数缺失")
    private Integer activeGoodsStock;

    @NotNull(message = "activeGoodsOrderMoney参数缺失")
    private BigDecimal activeGoodsOrderMoney;

    @NotNull(message = "activeGoodsOrderNum参数缺失")
    private Integer activeGoodsOrderNum;

    @NotNull(message = "activeGoodsMemberNum参数缺失")
    private Integer activeGoodsMemberNum;

    @NotNull(message = "activeGoodsSuccessNum参数缺失")
    private Integer activeGoodsSuccessNum;

}
