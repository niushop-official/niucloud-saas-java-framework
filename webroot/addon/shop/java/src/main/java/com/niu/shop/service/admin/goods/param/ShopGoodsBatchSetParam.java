package com.niu.shop.service.admin.goods.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class ShopGoodsBatchSetParam {
    private Boolean isAll = false;

    private String setType;

    private JSONArray goodsIds;

    private JSONObject setValue;

    private JSONObject where;
}
