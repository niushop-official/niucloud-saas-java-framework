package com.niu.shop.service.admin.marketing.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ShopCouponSendParam {
    private String rangeType;

    private JSONObject rangeParam;

    private Integer sendNum;
}
