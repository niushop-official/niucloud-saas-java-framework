package com.niu.core.service.admin.pay.param;

import lombok.Data;

@Data
public class GetFriendspayInfoByTradeParam {
    private String tradeType;
    private Integer tradeId;
    private String channel;
}
