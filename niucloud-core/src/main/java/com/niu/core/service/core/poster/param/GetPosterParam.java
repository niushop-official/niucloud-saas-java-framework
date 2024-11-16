package com.niu.core.service.core.poster.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class GetPosterParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer id;

    private String type;

    private Map<String, Object> param;

    private String channel;

    private Boolean isThrowException = false;
}
