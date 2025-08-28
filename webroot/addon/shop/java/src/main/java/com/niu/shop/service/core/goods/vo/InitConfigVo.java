package com.niu.shop.service.core.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class InitConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> sortType;

    private Map<String, String> sortColumn;

}
