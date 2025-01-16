package com.niu.shop.service.core.order.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class OrderBatchDeliveryRow {
    @ExcelProperty(value = "订单号", order = 1)
    private String orderNo;
    @ExcelProperty(value = "订单项ID", order = 2)
    private String orderItemId;
    @ExcelProperty(value = "物流公司", order = 3)
    private String companyName;
    @ExcelProperty(value = "物流单号", order = 4)
    private String expressNo;
}
