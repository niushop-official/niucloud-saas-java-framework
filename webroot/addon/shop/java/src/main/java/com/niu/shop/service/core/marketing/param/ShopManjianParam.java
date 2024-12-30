package com.niu.shop.service.core.marketing.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 满减活动表参数
 */
@Data
public class ShopManjianParam implements Serializable {

    private Integer manjianId;
    private String conditionType;
    private String endTime;
    private JSONArray goodsIds;
    private JSONArray goodsData;
    private String goodsType;
    private String joinMemberType;
    private String manjianName;
    private String[] manjianTime;
    private JSONArray labelIds;
    private JSONArray levelIds;
    private String remark;
    private JSONArray ruleJson;
    private String ruleType;
    private String startTime;

}
