package com.niu.shop.service.core.refund.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActiveRefundCloseParam {
    private String mainType;

    private Integer mainId;

    private Integer siteId;

    private String orderRefundNo;
}
