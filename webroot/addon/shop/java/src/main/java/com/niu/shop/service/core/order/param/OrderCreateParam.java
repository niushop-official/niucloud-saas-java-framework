package com.niu.shop.service.core.order.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class OrderCreateParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "order_key参数缺失")
    @NotEmpty(message = "order_key不能为空")
    private String orderKey;

    private String memberRemark = "";

    private InvoiceCreateParam invoice;

    private JSONObject formData;

}
