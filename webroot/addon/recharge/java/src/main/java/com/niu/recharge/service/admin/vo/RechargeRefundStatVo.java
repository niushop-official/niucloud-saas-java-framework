package com.niu.recharge.service.admin.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeRefundStatVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Stat all;

    private Stat have;

    @Data
    public static class Stat {
        private String name;

        private String key;

        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal money = new BigDecimal(0);
    }
}
