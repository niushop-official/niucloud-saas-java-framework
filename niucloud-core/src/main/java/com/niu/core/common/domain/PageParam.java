package com.niu.core.common.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    // 页码
    private Integer page = 1;
    // 每页数量
    private Integer limit = 10;
}
