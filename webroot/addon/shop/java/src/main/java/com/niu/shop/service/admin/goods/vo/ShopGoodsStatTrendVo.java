package com.niu.shop.service.admin.goods.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopGoodsStatTrendVo {
    private List<DataItem> data;
    private List<String> xAxis;

    @Data
    public static class DataItem {
        private String name;
        private List<BigDecimal> data;
    }
}
