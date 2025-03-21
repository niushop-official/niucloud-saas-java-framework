package com.niu.shop.listener.treasure;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetTreasureSelectListVo {
    private Integer treasureId;
    private Integer siteId;
    private String treasureName;
    private String treasureSubName;
    private String treasureImage;
    private BigDecimal treasurePrice;
    private String treasureUrl;
    private Integer relateId;
    private String relateType;
    private String createTime;
    private Integer isJoin;
    private String relateTypeName;
}
