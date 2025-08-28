package com.niu.shop.service.core.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SortConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sortType;

    private int defaultSort;

    private InitConfigVo init;

    private String sortColumn;
}
