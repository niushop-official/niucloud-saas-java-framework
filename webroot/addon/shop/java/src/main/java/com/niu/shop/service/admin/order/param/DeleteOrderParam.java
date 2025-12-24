package com.niu.shop.service.admin.order.param;

import lombok.Data;

import java.util.List;

@Data
public class DeleteOrderParam {

    private List<Integer> orderIds;
}
