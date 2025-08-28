package com.niu.core.service.core.poster.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddPosterParam {
    private Integer siteId;
    private String addon;
    private String name;
    private String type;
    private String value;
    private Integer status;
    private Integer isDefault;
}
