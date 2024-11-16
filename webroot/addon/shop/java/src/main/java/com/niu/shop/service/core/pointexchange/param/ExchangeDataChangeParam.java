package com.niu.shop.service.core.pointexchange.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExchangeDataChangeParam  implements Serializable {
    protected static final long serialVersionUID = 1L;

    private Integer totalPointNum; // 积分消费总额
    private BigDecimal totalPriceNum; // 总支付金额
    private Integer totalOrderNum; // 订单笔数
    private Integer totalMemberNum; // 参与会员数
    private Integer totalExchangeNum;
}
