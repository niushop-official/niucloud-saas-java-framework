package com.niu.shop.service.admin.goods.vo;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopGoodsRankOptionData {
    private JSONObject rankType;
    private JSONObject goodsSource;
    private JSONObject ruleType;
}
