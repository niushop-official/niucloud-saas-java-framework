package com.niu.shop.service.core.shop.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatParam {
    private Long time;
    private Integer siteId;
    private Integer orderNum;
}
