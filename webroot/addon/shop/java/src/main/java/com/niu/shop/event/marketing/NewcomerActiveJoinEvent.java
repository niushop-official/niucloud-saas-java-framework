package com.niu.shop.event.marketing;

import lombok.Data;

@Data
public class NewcomerActiveJoinEvent {
    private Integer siteId;
    private Integer memberId;
    private Integer isJoin;
    private Integer orderId;
}
