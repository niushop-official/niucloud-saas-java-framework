package com.niu.core.service.core.poster.param;

import lombok.Data;

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
