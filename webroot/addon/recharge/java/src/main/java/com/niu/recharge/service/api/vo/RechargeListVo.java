package com.niu.recharge.service.api.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class RechargeListVo {
    @JsonProperty("recharge_id")
    private Integer rechargeId;
    @JsonProperty("recharge_name")
    private String rechargeName;
    @JsonProperty("face_value")
    private String faceValue;
    @JsonProperty("buy_price")
    private String buyPrice;
    @JsonProperty("point")
    private Integer point;
    @JsonProperty("growth")
    private Integer growth;
    @JsonProperty("gift_content")
    private List<?> giftContent;
}
