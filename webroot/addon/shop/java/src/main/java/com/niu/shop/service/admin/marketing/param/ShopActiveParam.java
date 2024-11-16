package com.niu.shop.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 店铺营销活动（整体活动）参数
*/
@Data
public class ShopActiveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "activeId参数缺失")
    private Integer activeId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "activeName参数缺失")
    private String activeName;

    @NotNull(message = "activeDesc参数缺失")
    private String activeDesc;

    @NotNull(message = "activeType参数缺失")
    private String activeType;

    @NotNull(message = "activeGoodsType参数缺失")
    private String activeGoodsType;

    @NotNull(message = "activeGoodsInfo参数缺失")
    private String activeGoodsInfo;

    @NotNull(message = "activeClass参数缺失")
    private String activeClass;

    @NotNull(message = "activeClassCategory参数缺失")
    private String activeClassCategory;

    @NotNull(message = "relateMember参数缺失")
    private String relateMember;

    @NotNull(message = "activeValue参数缺失")
    private String activeValue;

    @NotNull(message = "startTime参数缺失")
    private Long startTime;

    @NotNull(message = "endTime参数缺失")
    private Long endTime;

    @NotNull(message = "activeStatus参数缺失")
    private String activeStatus;

    @NotNull(message = "activeOrderMoney参数缺失")
    private BigDecimal activeOrderMoney;

    @NotNull(message = "activeOrderNum参数缺失")
    private Integer activeOrderNum;

    @NotNull(message = "activeMemberNum参数缺失")
    private Integer activeMemberNum;

    @NotNull(message = "activeSuccessNum参数缺失")
    private Integer activeSuccessNum;

}
