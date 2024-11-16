package com.niu.recharge.service.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.enums.OrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RechargeOrderInfoVo extends RechargeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RechargeOrderItem> item;

    private Status orderStatusInfo;

    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal orderMoney;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    @Data
    private static class Status {
        private String name;

        private Integer status;
    }

    public Status getOrderStatusInfo() {
        Status status = new Status();
        status.setStatus(this.getOrderStatus());
        status.setName(OrderStatusEnum.getNameByStatus(this.getOrderStatus()));
        return status;
    }
}
