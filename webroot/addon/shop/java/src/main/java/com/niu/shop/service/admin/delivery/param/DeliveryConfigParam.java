package com.niu.shop.service.admin.delivery.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 配送方式设置参数
*/
@Data
public class DeliveryConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private JSONArray value;

}
