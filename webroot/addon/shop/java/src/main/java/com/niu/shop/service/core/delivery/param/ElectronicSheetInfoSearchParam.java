package com.niu.shop.service.core.delivery.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ElectronicSheetInfoSearchParam {
    private Integer siteId;
    private List<Integer> ids;
    private Integer status;
    private Integer isDefault;
}
